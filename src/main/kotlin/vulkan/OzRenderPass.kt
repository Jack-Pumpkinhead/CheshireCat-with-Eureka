package vulkan

import org.lwjgl.vulkan.VK10
import vkk.*
import vkk.entities.VkRenderPass
import vkk.vk10.createRenderPass
import vkk.vk10.structs.*

class OzRenderPass(val device: OzDevice, format: VkFormat) {

    val renderpass: VkRenderPass

    init {
        val attachment_0 = AttachmentDescription(
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

        val subpass_0 = SubpassDescription(
            pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
            colorAttachments = arrayOf(attachmentReference) //The index of the attachment in this array is directly referenced from the fragment shader with the layout(location = 0) out vec4 outColor directive!
                                                            // layout in fragment shader --- colorAttachments     then map to attachments
        )

        val subpassDependency = SubpassDependency(
            srcSubpass = VK10.VK_SUBPASS_EXTERNAL,
            dstSubpass = 0,  //index of subpasses
            srcStageMask = VkPipelineStage.COLOR_ATTACHMENT_OUTPUT_BIT.i,
            dstStageMask = VkPipelineStage.COLOR_ATTACHMENT_OUTPUT_BIT.i,
            srcAccessMask = VkAccess(0).i,
            dstAccessMask = VkAccess.COLOR_ATTACHMENT_WRITE_BIT.i
        )

        //用subpasses可以做到分几次绘制 (最后绘制界面之类)
        val renderPassCI = RenderPassCreateInfo(
            attachments = arrayOf(attachment_0),    //only images can be used for attachments. attachments are descriptions of resources used during rendering.
                                                    //renderpass.attachments --- framebuffer.attachments      lines up
            subpasses = arrayOf(subpass_0),
            dependencies = arrayOf(subpassDependency)
        )

        renderpass = device.device.createRenderPass(renderPassCI)

    }

    fun destroy() {
        device.device.destroy(renderpass)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }


}