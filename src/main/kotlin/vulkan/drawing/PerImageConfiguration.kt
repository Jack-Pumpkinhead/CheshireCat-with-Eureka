package vulkan.drawing

import game.main.Recorder
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.lwjgl.vulkan.VK10
import vkk.VkResult
import vkk.entities.*
import vkk.extensions.PresentInfoKHR
import vkk.identifiers.CommandBuffer
import vkk.vk10.structs.SubmitInfo
import vulkan.*
import vulkan.image.DepthImage

/**
 * Created by CowardlyLion on 2020/6/3 22:23
 */
@Deprecated("old")
class PerImageConfiguration(
    val index: Int,
    val image: VkImage,
    val imageView: VkImageView,
    val depth: DepthImage,
//    val commandBuffer: CommandBuffer,
    val framebuffer: OzFramebuffer,
    val queues: OzQueues,
    val swapchain: OzSwapchain,
    val commandPools: OzCommandPools,
    device: OzDevice
) {

    val submitS = device.semaphore()

    val mutex = Mutex()
    private val cmds = mutableListOf<Recorder>()

    suspend fun withCmds(action: (MutableList<Recorder>) -> Unit) {
        mutex.withLock {
            action(cmds)
        }
    }


    suspend fun submit(acquiredS: VkSemaphore): Boolean {
        val cb = commandPools.graphicMutableCP.allocate().await()
        mutex.withLock {
            framebuffer.withRenderpass(cb) {cb->
                cmds.forEach { action->
                    action(cb)
                }
            }
        }

        val submitInfo = SubmitInfo(
            waitSemaphoreCount = 1,
            waitSemaphores = VkSemaphore_Array(arrayListOf(acquiredS)),
            waitDstStageMask = intArrayOf(VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),  //present operation will access image at this stage
            commandBuffers = arrayOf(cb),  //Submitting work to command lists doesn’t start any work on the GPU.
            signalSemaphores = VkSemaphore_Array(arrayListOf(submitS))
        )
        val vkResult = queues.graphicQ.submit(
            submitInfo
        ).await()
        commandPools.graphicMutableCP.free(cb)

        if (!checkResult(vkResult, "submit")) {
            //not sure need reset submitS or not.
            return false
        }

        val presentInfo = PresentInfoKHR(
            waitSemaphores = VkSemaphore_Array(arrayListOf(submitS)),
            swapchains = VkSwapchainKHR_Array(arrayListOf(swapchain.swapchain)),
            imageIndices = intArrayOf(index),
            results = null
        )

        val result = queues.presentQ.present(presentInfo).await()
        if (!checkResult(result, "present")) {
            return false
        }
        return true
        
    }


    fun checkResult(result: VkResult,state:String): Boolean {
        if (result == VkResult.ERROR_OUT_OF_DATE_KHR) {
            OzVulkan.logger.warn("$index, $state: out of date")
            return false
        } else if (result != VkResult.SUCCESS && result != VkResult.SUBOPTIMAL_KHR) {
            OzVulkan.logger.warn("$index, $state: no success and no suboptimal")
            return  false
        }
        return true
    }

}