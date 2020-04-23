package vulkan

import mu.KotlinLogging
import vkk.entities.VkFramebuffer
import vkk.entities.VkFramebuffer_Array
import vkk.entities.VkImageView
import vkk.entities.VkImageView_Array
import vkk.vk10.createFramebuffer
import vkk.vk10.createFramebufferArray
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.FramebufferCreateInfo

class OzFramebuffers(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val renderPass: OzRenderPass,
    val swapchain: OzSwapchain,
    val imageViews: OzImageViews
) {

    val logger = KotlinLogging.logger { }

    val framebuffers: VkFramebuffer_Array

    init {

        val framebufferCI = FramebufferCreateInfo(
            renderPass = renderPass.renderpass,
            width = swapchain.extent.width,
            height = swapchain.extent.height,
            layers = 1  //Image layer, not debug layer
        )
        framebuffers = device.device.createFramebufferArray(
            createInfo = framebufferCI,
            imageViews = imageViews.imageViews
        )

    }


    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        framebuffers.indices.forEach { device.device.destroy(framebuffers[it]) }
    }

}