package vulkan

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import mu.KotlinLogging
import vkk.entities.VkFramebuffer
import vkk.entities.VkImageView_Array
import vkk.vk10.createFramebuffer
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.FramebufferCreateInfo
import vulkan.concurrent.OzFramebuffer

class OzFramebuffers(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val renderPass: OzRenderPass,
    val imageViews: OzImageViews,
    extent2D: Extent2D
) {
    companion object {
        val logger = KotlinLogging.logger { }
    }


    val fbs = imageViews.imageViews.map {
        OzFramebuffer(ozVulkan, device, renderPass, it, extent2D)
    }

    suspend fun onRecreateRenderpass(job: CompletableJob): List<Job> {
        return fbs.map {
            it.wait_clear(job)  //如果重建OzFramebuffers就不需要clear
        }
    }






}