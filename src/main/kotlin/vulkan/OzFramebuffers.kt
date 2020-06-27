package vulkan

import vkk.vk10.structs.Extent2D

class OzFramebuffers(
    val device: OzDevice,
    val swapchain: OzSwapchain,
    renderpass: OzRenderPasses,
    extent2D: Extent2D
) {

    val fb_simple = swapchain.imageViews.map { imageView ->
        OzFramebuffer(device, renderpass.renderpass, listOf(imageView), extent2D)
    }
    val fb_depth = swapchain.imageViews.zip(swapchain.depth).map { (imageView, depth) ->
        OzFramebuffer(device, renderpass.renderpass_depth, listOf(imageView, depth.imageView), extent2D)
    }


    fun destroy() {
        fb_simple.forEach {
            it.destroy()
        }
        fb_depth.forEach {
            it.destroy()
        }
        OzVulkan.logger.info {
            "framebuffers destroyed"
        }
    }




}