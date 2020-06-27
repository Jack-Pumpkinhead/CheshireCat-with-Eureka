package vulkan

import org.lwjgl.vulkan.VK10
import vkk.*
import vkk.entities.VkRenderPass
import vkk.vk10.createRenderPass
import vkk.vk10.structs.*

class OzRenderPasses(val device: OzDevice, format: VkFormat) {

    val renderpass: VkRenderPass
    val renderpass_depth: VkRenderPass

    fun presentableAttach(format: VkFormat) = AttachmentDescription(
        format = format,
        samples = VkSampleCount._1_BIT,

        loadOp = VkAttachmentLoadOp.CLEAR,
        storeOp = VkAttachmentStoreOp.STORE,

        stencilLoadOp = VkAttachmentLoadOp.DONT_CARE,
        stencilStoreOp = VkAttachmentStoreOp.DONT_CARE,

        initialLayout = VkImageLayout.UNDEFINED,
        finalLayout = VkImageLayout.PRESENT_SRC_KHR
    )
    fun depthAttach(format: VkFormat) = AttachmentDescription(
        format = format,
        samples = VkSampleCount._1_BIT,

        loadOp = VkAttachmentLoadOp.CLEAR,
        storeOp = VkAttachmentStoreOp.DONT_CARE,

        stencilLoadOp = VkAttachmentLoadOp.CLEAR,
        stencilStoreOp = VkAttachmentStoreOp.DONT_CARE,

        initialLayout = VkImageLayout.UNDEFINED,
        finalLayout = VkImageLayout.DEPTH_STENCIL_ATTACHMENT_OPTIMAL
    )

    val colorRef0 = AttachmentReference(
        attachment = 0, //index of attachmentDescription array  //of what?
        layout = VkImageLayout.COLOR_ATTACHMENT_OPTIMAL
    )
    val depthRef1 = AttachmentReference(
        attachment = 1, //index of attachmentDescription array  //of what?
        layout = VkImageLayout.DEPTH_STENCIL_ATTACHMENT_OPTIMAL
    )

    val subpassDependency = SubpassDependency(
        srcSubpass = VK10.VK_SUBPASS_EXTERNAL,
        dstSubpass = 0,  //index of subpasses
        srcStageMask = VkPipelineStage.COLOR_ATTACHMENT_OUTPUT_BIT.i,
        dstStageMask = VkPipelineStage.COLOR_ATTACHMENT_OUTPUT_BIT.i,
        srcAccessMask = VkAccess(0).i,
        dstAccessMask = VkAccess.COLOR_ATTACHMENT_WRITE_BIT.i
    )
    val depthDependencyRead = SubpassDependency(    //有用不?
        srcSubpass = VK10.VK_SUBPASS_EXTERNAL,
        dstSubpass = 0,  //index of subpasses
        srcStageMask = VkPipelineStage.EARLY_FRAGMENT_TESTS_BIT.i,
        dstStageMask = VkPipelineStage.EARLY_FRAGMENT_TESTS_BIT.i,
        srcAccessMask = VkAccess(0).i,
        dstAccessMask = VkAccess.DEPTH_STENCIL_ATTACHMENT_READ_BIT.i
    )
    val depthDependencyWrite = SubpassDependency(
        srcSubpass = VK10.VK_SUBPASS_EXTERNAL,
        dstSubpass = 0,  //index of subpasses
        srcStageMask = VkPipelineStage.LATE_FRAGMENT_TESTS_BIT.i,
        dstStageMask = VkPipelineStage.LATE_FRAGMENT_TESTS_BIT.i,
        srcAccessMask = VkAccess(0).i,
        dstAccessMask = VkAccess.DEPTH_STENCIL_ATTACHMENT_WRITE_BIT.i
    )



    init {


        val subpass_0 = SubpassDescription(
            pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
            colorAttachments = arrayOf(colorRef0), //The index of the attachment in this array is directly referenced from the fragment shader with the layout(location = 0) out vec4 outColor directive!
            // layout in fragment shader --- colorAttachments     then map to attachments
            depthStencilAttachment = null,
            inputAttachments = null,
            resolveAttachments = null,
            preserveAttachments = null
        )



        //用subpasses可以做到分几次绘制 (最后绘制界面之类)
        renderpass = create(
            attachments = arrayOf(presentableAttach(format)),    //only images can be used for attachments. attachments are descriptions of resources used during rendering.
                                                    //renderpass.attachments --- framebuffer.attachments      lines up
            subpasses = arrayOf(subpass_0),
            dependencies = arrayOf(subpassDependency)
        )



        val subpass_0_depth = SubpassDescription(
            pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
            colorAttachments = arrayOf(colorRef0), //The index of the attachment in this array is directly referenced from the fragment shader with the layout(location = 0) out vec4 outColor directive
            // layout in fragment shader --- colorAttachments     then map to attachments
            depthStencilAttachment = depthRef1,  //can use only one
            inputAttachments = null,
            resolveAttachments = null,
            preserveAttachments = null
        )



        renderpass_depth = create(
            attachments = arrayOf(presentableAttach(format), depthAttach(device.physicalDevice.depthFormat)),
            subpasses = arrayOf(subpass_0_depth),
//            dependencies = arrayOf(subpassDependency,depthDependencyRead,depthDependencyWrite)
            dependencies = arrayOf(subpassDependency)
        )




    }

    fun create(
        attachments: Array<AttachmentDescription>? = null,
        subpasses: Array<SubpassDescription>,
        dependencies: Array<SubpassDependency>? = null
    ) = device.device.createRenderPass(
        RenderPassCreateInfo(
            attachments = attachments,
            subpasses = subpasses,
            dependencies = dependencies
        )
    )

    fun destroy() {
        device.device.destroy(renderpass)
        device.device.destroy(renderpass_depth)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }


}