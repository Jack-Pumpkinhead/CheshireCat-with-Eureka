package vulkan.drawing

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.lwjgl.vulkan.VK10
import vkk.VkResult
import vkk.entities.VkFence
import vkk.entities.VkSemaphore
import vkk.entities.VkSemaphore_Array
import vkk.entities.VkSwapchainKHR_Array
import vkk.extensions.PresentInfoKHR
import vkk.extensions.acquireNextImageKHR
import vkk.vk10.structs.SubmitInfo
import vulkan.OzDevice
import vulkan.OzQueues
import vulkan.OzSwapchain
import vulkan.OzVulkan
import vulkan.pipelines.layout.OzUniformMatrixDynamic

/**
 * Created by CowardlyLion on 2020/5/8 21:55
 */
class DrawImage(
    val swapchain: OzSwapchain,
    val device: OzDevice,
    val uniformMatrixDynamic: OzUniformMatrixDynamic,
    val queues: OzQueues,
    val ozObjects: OzObjects
) {
    val submitJob: Array<CompletableDeferred<VkResult>> = Array(swapchain.images.size) { CompletableDeferred(VkResult.SUCCESS) }
    val submitS: Array<VkSemaphore> = Array(swapchain.images.size) { device.semaphore() }
    val acquiredS = device.semaphore()


    fun drawImage(): Boolean {


        var success = true
        val imageIndex = device.device.acquireNextImageKHR(
            swapchain = swapchain.swapchain,
            timeout = -1L,
            semaphore = acquiredS,
            fence = VkFence.NULL,
            check = {
                if (it == VkResult.ERROR_OUT_OF_DATE_KHR) {
                    OzVulkan.logger.info("recreate swapchain after acquire")
                    success = false
                } else if (it != VkResult.SUCCESS && it != VkResult.SUBOPTIMAL_KHR) {
                    OzVulkan.logger.error { it.description }
                    OzVulkan.logger.info("recreate swapchain after acquire")
                    success = false
                }
            }
        )

        if(!success) return false

//        ozVulkan.uniformBuffer.update(imageIndex, Extent2D(ozVulkan.window.framebufferSize))
//        ozVulkan.descriptorSets.update()
//        uniformMatrixDynamic.update(imageIndex)



        val cmds = runBlocking {
            ozObjects.refresh(imageIndex)
            submitJob[imageIndex].await()
            swapchain.images[imageIndex].getDrawCmds()
        }

//        device.device.waitForFences(submitF[imageIndex], true, -1L)
//        device.device.resetFences(submitF[imageIndex])



        val submitInfo = SubmitInfo(
            waitSemaphoreCount = 1,
            waitSemaphores = VkSemaphore_Array(arrayListOf(acquiredS)),
            waitDstStageMask = intArrayOf(VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),  //present operation will access image at this stage
            commandBuffers = cmds,  //Submitting work to command lists doesn’t start any work on the GPU.
            signalSemaphores = VkSemaphore_Array(arrayListOf(submitS[imageIndex]))
        )
        runBlocking {
            submitJob[imageIndex] = queues.graphicQ.submit(
                submitInfo    //current vkk api doesn't support multiple submitInfo, so flatten cmds in one array
            )
        }


        val presentInfo = PresentInfoKHR(
            waitSemaphores = VkSemaphore_Array(arrayListOf(submitS[imageIndex])),
            swapchains = VkSwapchainKHR_Array(arrayListOf(swapchain.swapchain)),
            imageIndices = intArrayOf(imageIndex),
            results = null
        )

        val result = runBlocking {
            queues.presentQ.present(presentInfo).await()
        }


        if (result == VkResult.ERROR_OUT_OF_DATE_KHR || result == VkResult.SUBOPTIMAL_KHR) {
            OzVulkan.logger.info("recreate swapchain/renderpass after present")
            return false
        } else if (result != VkResult.SUCCESS) {
            OzVulkan.logger.error("present swapchain fail")
            return false
        }

        return true
    }


    fun destroy() {
        submitS.forEach {
            device.device.destroy(it)
        }
        OzVulkan.logger.info {
            "drawImage destroyed"
        }
    }
}