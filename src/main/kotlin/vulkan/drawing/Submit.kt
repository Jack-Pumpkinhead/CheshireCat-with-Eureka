package vulkan.drawing

import org.lwjgl.vulkan.VK10
import vkk.VkResult
import vkk.entities.VkSemaphore
import vkk.entities.VkSemaphore_Array
import vkk.entities.VkSwapchainKHR_Array
import vkk.extensions.PresentInfoKHR
import vkk.identifiers.CommandBuffer
import vkk.vk10.structs.SubmitInfo
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/6/8 17:19
 */
class Submit(val vulkan: OzVulkan) {

    val submitS = vulkan.device.semaphore()

    suspend fun submit(commandBuffer: CommandBuffer, acquiredS: VkSemaphore, index:Int): Boolean {

        val submitInfo = SubmitInfo(
            waitSemaphoreCount = 1,
            waitSemaphores = VkSemaphore_Array(arrayListOf(acquiredS)),
            waitDstStageMask = intArrayOf(VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),  //present operation will access image at this stage
            commandBuffers = arrayOf(commandBuffer),  //Submitting work to command lists doesn’t start any work on the GPU.
            signalSemaphores = VkSemaphore_Array(arrayListOf(submitS))
        )
        val vkResult = vulkan.queues.graphicQ.submit(submitInfo).await()

        vulkan.commandpools.graphicMutableCP.free(commandBuffer)

        if (!checkResult(vkResult, "submit", index)) {
            //not sure need reset submitS or not.
            return false
        }

        val presentInfo = PresentInfoKHR(
            waitSemaphores = VkSemaphore_Array(arrayListOf(submitS)),
            swapchains = VkSwapchainKHR_Array(arrayListOf(vulkan.swapchain.swapchain)),
            imageIndices = intArrayOf(index),
            results = null
        )

        val result = vulkan.queues.presentQ.present(presentInfo).await()
        if (!checkResult(result, "present", index)) {
            return false
        }
        return true

    }

    fun checkResult(result: VkResult, state: String, index: Int): Boolean {
        if (result == VkResult.ERROR_OUT_OF_DATE_KHR) {
            OzVulkan.logger.warn("$index, $state: out of date")
            return false
        } else if (result != VkResult.SUCCESS && result != VkResult.SUBOPTIMAL_KHR) {
            OzVulkan.logger.warn("$index, $state: ${result.description}")
            return false
        }
        return true
    }


}