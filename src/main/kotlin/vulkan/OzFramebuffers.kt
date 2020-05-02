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

    val logger = KotlinLogging.logger { }

    val framebuffers: List<VkFramebuffer>

    init {
        framebuffers = imageViews.imageViews.map {
            val framebufferCI = FramebufferCreateInfo(
                renderPass = renderPass.renderpass,
                attachments = VkImageView_Array(listOf(it)),
                width = extent2D.width,
                height = extent2D.height,
                layers = 1  //Image layer, not debug layer
            )
            device.device.createFramebuffer(framebufferCI)
        }

    }

    val fbs = imageViews.imageViews.map {
        OzFramebuffer(ozVulkan, device, renderPass, it, extent2D)
    }

    suspend fun onRecreateRenderpass(job: CompletableJob): List<Job> {
        return fbs.map {
            it.wait_clear(job)
        }
    }




    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        framebuffers.indices.forEach { device.device.destroy(framebuffers[it]) }
    }

}