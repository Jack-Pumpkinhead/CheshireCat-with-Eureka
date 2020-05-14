package vulkan

import vkk.entities.VkImageView_Array
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.FramebufferCreateInfo

class OzFramebuffers(
    val device: OzDevice,
    imageViews: OzImageViews,
    renderpass: OzRenderPass,
    extent2D: Extent2D
) {

    val fbs = imageViews.imageViews.map {
        OzFramebuffer(
            device,
            FramebufferCreateInfo(
                renderPass = renderpass.renderpass,
                attachments = VkImageView_Array(listOf(it)),   //VkFramebuffer defines which VkImageView is to be which attachment.
                width = extent2D.width,
                height = extent2D.height,
                layers = 1  //Image layer, not debug layer
            )
        )
    }


    fun destroy() {
        fbs.forEach {
            it.destroy()
        }
        OzVulkan.logger.info {
            "framebuffers destroyed"
        }
    }




}