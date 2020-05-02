package vulkan.concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.selects.select
import mu.KotlinLogging
import org.lwjgl.vulkan.VK10
import vkk.VkResult
import vkk.entities.*
import vkk.extensions.PresentInfoKHR
import vkk.extensions.acquireNextImageKHR
import vkk.extensions.presentKHR
import vkk.identifiers.Queue
import vkk.vk10.getQueue
import vkk.vk10.resetFences
import vkk.vk10.structs.SubmitInfo
import vkk.vk10.submit
import vkk.vk10.waitForFences
import vulkan.OzDevice
import vulkan.OzVulkan

class OzQueue(val ozVulkan: OzVulkan, val device: OzDevice, val queueFamilyIndex: Int, val queueIndex: Int) {

    sealed class Action {
        class Submit(val info: SubmitInfo, val resp: CompletableDeferred<VkResult>) : Action()
        class Present(val info: PresentInfoKHR, val resp: CompletableDeferred<VkResult>) : Action()
    }

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val queue: Queue = device.device.getQueue(queueFamilyIndex, queueIndex)

    suspend fun submit(info: SubmitInfo): CompletableDeferred<VkResult> {
        val resp = CompletableDeferred<VkResult>()
        actor.send(Action.Submit(info, resp))
        return resp
    }


    private val wait = Channel<Pair<Job, CompletableJob>>(Channel.UNLIMITED)
    suspend fun wait(toWait: Job): Job {
        val received = Job()
        wait.send(Pair(toWait, received))
        return received
    }
    private val wait_clear = Channel<Triple<Job, CompletableJob, CompletableJob>>(Channel.UNLIMITED)
    suspend fun wait_clear(toWait: Job): Job {
        val received = Job()
        val reset = Job()
        wait_clear.send(Triple(toWait, received, reset))
        received.join()
        return reset
    }

    val actor = device.scope.actor<Action> {
        val fence = device.signaledFence()

        while (isActive) {
            select<Unit> {
                wait_clear.onReceive { (toWait, received, clear) -> //两次握手
                    received.complete()
                    toWait.join()
                    while (!channel.isEmpty) {
                        channel.poll()
                    }
                    clear.complete()
                }
                wait.onReceive { (toWait, received) ->
                    received.complete()
                    toWait.join()
                }
                channel.onReceive {
                    when (it) {
                        is Action.Submit -> {
                            device.device.resetFences(fence)
                            val result = queue.submit(it.info, fence)
                            device.device.waitForFences(fence, true, -1)
                            it.resp.complete(result)
                        }
                        is Action.Present -> {
                            val result = queue.presentKHR(it.info)
                            it.resp.complete(result)
                        }
                    }
                }
            }
        }
        device.device.destroy(fence)
    }


//    val workers = ozVulkan.surfaceSupport.imageCount - 1    //max num of drawing images at the same time

//    val workingFence: List<VkFence> = List(workers) {device.signaledFence() }
//    val workingSemaphore: List<VkSemaphore> = List(workers) { device.semaphore() }
//    val workingFenceArray = VkFence_Array(workingFence)

    //worker on image
    val submitF: Array<VkFence> = Array(ozVulkan.surfaceSupport.imageCount) { device.signaledFence() }
    val presentJ: Array<CompletableJob> = Array(ozVulkan.surfaceSupport.imageCount) { Job() }
    val submitS: Array<VkSemaphore> = Array(ozVulkan.surfaceSupport.imageCount) { device.semaphore() }
    val acquiredS = device.semaphore()
/*
    fun findIdleFence(): Int {
        device.device.waitForFences(
            fences = workingFenceArray,
            waitAll = false,
            timeout = -1L
        )
        return workingFence.indexOfFirst {
            device.device.getFenceStatus(it) == VkResult.SUCCESS
        }
    }*/

    init {
        for (i in presentJ.indices) {
            presentJ[i].complete()
        }
    }

    var ii = 0

    fun drawImage(): Boolean {

//        val workerIndex = findIdleFence()
//        device.device.resetFences(workingFence[workerIndex])


        var success = true
        val imageIndex = device.device.acquireNextImageKHR(
            swapchain = ozVulkan.swapchain.swapchain,
            timeout = -1L,
            semaphore = acquiredS,
            fence = VkFence.NULL,
            check = {
                if (it == VkResult.ERROR_OUT_OF_DATE_KHR) {
                    logger.info("recreate swapchain after acquire")
                    success = false
                } else if (it != VkResult.SUCCESS && it != VkResult.SUBOPTIMAL_KHR) {
                    logger.error { it.description }
                    logger.info("recreate swapchain after acquire")
                    success = false
                }
            }
        )

//        logger.info {
//            "acquire $success"
//        }


        if(!success) return false


        val cmd_defer = runBlocking { ozVulkan.framebuffer.fbs[imageIndex].getCmds() }




//        if (imageStatusFence[imageIndex] != VkFence.NULL) {
//            logger.info {
//                "waiting"
//            }

//            device.device.waitForFences(imageF[imageIndex], true, -1L)

        runBlocking {
            presentJ[imageIndex].join()
            presentJ[imageIndex] = Job()
        }
//        }
//        imageStatusFence[imageIndex] = workingFence[workerIndex]

        device.device.resetFences(submitF[imageIndex])

//        logger.info {
//            "previous fence finish"
//        }


        val cmds = runBlocking {
            cmd_defer.await()
        }

        ii++
        if (ii % 10000 == 0) {
            logger.info {
                "cmds size: ${cmds.size}"
            }
        }



        val submitInfo = SubmitInfo(
            waitSemaphoreCount = 1,
            waitSemaphores = VkSemaphore_Array(arrayListOf(acquiredS)),
            waitDstStageMask = intArrayOf(VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),  //present operation will access image at this stage
            commandBuffers = cmds,
            signalSemaphores = VkSemaphore_Array(arrayListOf(submitS[imageIndex]))
        )
        queue.submit(
            submit = submitInfo,    //current vkk api doesn't support multiple submitInfo, so flatten cmds in one array
            fence = submitF[imageIndex]
        )

//        logger.info {
//            "after submit"
//        }


        val presentInfo = PresentInfoKHR(
            waitSemaphores = VkSemaphore_Array(arrayListOf(submitS[imageIndex])),
            swapchains = VkSwapchainKHR_Array(arrayListOf(ozVulkan.swapchain.swapchain)),
            imageIndices = intArrayOf(imageIndex),
            results = null
        )

        val result = runBlocking {
            val job = CompletableDeferred<VkResult>()
            device.presentQ.actor.send(Action.Present(presentInfo, job))
            job.await()
        }

//        logger.info {
//            "await present"
//        }


        if (result == VkResult.ERROR_OUT_OF_DATE_KHR || result == VkResult.SUBOPTIMAL_KHR) {
            logger.info("recreate swapchain/renderpass after present")
            return false
        } else if (result != VkResult.SUCCESS) {
            logger.error("present swapchain fail")
            return false
        }

        presentJ[imageIndex].complete()

        return true
    }



    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
//        actor.close()
        submitF.forEach {
            device.device.destroy(it)
        }
        submitS.forEach {
            device.device.destroy(it)
        }
        device.device.destroy(acquiredS)
        /*workingFence.forEach {
            device.device.destroy(it)
        }
        workingSemaphore.forEach {
            device.device.destroy(it)
        }*/
//        imageF.forEach {
//            device.device.destroy(it)
//        }
    }
}