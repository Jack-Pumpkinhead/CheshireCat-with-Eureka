package vulkan

import mu.KotlinLogging
import vkk.entities.VkFramebuffer
import vkk.entities.VkImageView
import vkk.entities.VkImageView_Array
import vkk.vk10.createFramebuffer
import vkk.vk10.structs.FramebufferCreateInfo

class OzFramebuffer(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val swapchain: OzSwapchain,
    val renderPass: OzRenderPass,
    val imageviewArray: VkImageView_Array
) {

    constructor(
        ozVulkan: OzVulkan,
        device: OzDevice,
        swapchain: OzSwapchain,
        renderPass: OzRenderPass, imageView: VkImageView
    ) : this(
        ozVulkan, device, swapchain, renderPass, VkImageView_Array(
            arrayListOf(imageView)
        )
    )

    val logger = KotlinLogging.logger { }

    val createInfo = FramebufferCreateInfo(
        renderPass = renderPass.renderpass,
        attachments = imageviewArray,
        width = swapchain.extent.width,
        height = swapchain.extent.height,
        layers = 1  //Image layer, not debug layer
    )

    val framebuffer = device.device.createFramebuffer(createInfo)

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(renderPass::destroy, this::destroy)
    }

    fun destroy() {
        device.device.destroy(framebuffer)
    }

}