package vulkan

import mu.KotlinLogging
import org.lwjgl.vulkan.VK10
import vkk.*
import vkk.entities.VkRenderPass
import vkk.vk10.createRenderPass
import vkk.vk10.structs.*

class OzRenderPass(val ozVulkan: OzVulkan, val device: OzDevice, format: VkFormat) {

    val logger = KotlinLogging.logger { }

    val renderpass: VkRenderPass

    init {
        val attachmentDescription = AttachmentDescription(
            format = format,
            samples = VkSampleCount._1_BIT,
            loadOp = VkAttachmentLoadOp.CLEAR,
            storeOp = VkAttachmentStoreOp.STORE,
            stencilLoadOp = VkAttachmentLoadOp.DONT_CARE,
            stencilStoreOp = VkAttachmentStoreOp.DONT_CARE,
            initialLayout = VkImageLayout.UNDEFINED,
            finalLayout = VkImageLayout.PRESENT_SRC_KHR
        )


        val attachmentReference = AttachmentReference(
            attachment = 0, //index of attachmentDescription array  //of what?
            layout = VkImageLayout.COLOR_ATTACHMENT_OPTIMAL
        )

        val subpassDescription = SubpassDescription(
            pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
            colorAttachments = arrayOf(attachmentReference) //The index of the attachment in this array is directly referenced from the fragment shader with the layout(location = 0) out vec4 outColor directive!
        )

        val subpassDependency = SubpassDependency(
            srcSubpass = VK10.VK_SUBPASS_EXTERNAL,
            dstSubpass = 0,  //index of subpasses
            srcStageMask = VkPipelineStage.COLOR_ATTACHMENT_OUTPUT_BIT.i,
            dstStageMask = VkPipelineStage.COLOR_ATTACHMENT_OUTPUT_BIT.i,
            srcAccessMask = VkAccess(0).i,
            dstAccessMask = VkAccess.COLOR_ATTACHMENT_WRITE_BIT.i
        )

        val renderPassCI = RenderPassCreateInfo(
            attachments = arrayOf(attachmentDescription),
            subpasses = arrayOf(subpassDescription),
            dependencies = arrayOf(subpassDependency)
        )

        renderpass = device.device.createRenderPass(renderPassCI)

    }

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        device.device.destroy(renderpass)
    }


}