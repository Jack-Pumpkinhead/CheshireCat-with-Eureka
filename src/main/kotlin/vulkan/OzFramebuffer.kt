package vulkan

import game.main.Recorder
import vkk.VkSubpassContents
import vkk.entities.*
import vkk.identifiers.CommandBuffer
import vkk.vk10.begin
import vkk.vk10.beginRenderPass
import vkk.vk10.clearDepthStencilImage
import vkk.vk10.createFramebuffer
import vkk.vk10.structs.*
import vulkan.OzDevice

class OzFramebuffer(
    val device: OzDevice,
    val createInfo: FramebufferCreateInfo
) {

    constructor(device: OzDevice,renderpass: VkRenderPass, imageViews: List<VkImageView>, extent2D: Extent2D):this(
        device, FramebufferCreateInfo(
            renderPass = renderpass,
            attachments = VkImageView_Array(imageViews),   //VkFramebuffer defines which VkImageView is to be which attachment.
            width = extent2D.width,
            height = extent2D.height,
            layers = 1  //Image layer, not debug layer
        )
    )


    val framebuffer: VkFramebuffer = device.device.createFramebuffer(createInfo)




    //drawing on full size
    fun beginRenderPass_Full(cb: CommandBuffer) {
//        val depthStencilClearValue = ClearValue()
//        depthStencilClearValue.depth = 0.35f      //自己写的bug2333

        cb.beginRenderPass(
            renderPassBegin = RenderPassBeginInfo(
                renderPass = createInfo.renderPass,
                framebuffer = framebuffer,
                renderArea = Rect2D(
                    offset = Offset2D(0, 0),
                    extent = Extent2D(createInfo.width, createInfo.height)
                ),
                clearValues = arrayOf(
                    ClearValue(0.0f, 0.0f, 0.0f, 1.0f),
                    ClearValue(1.0f, 0.0f, 0.0f, 0.0f)  //对应attachments
//                    ClearDepthStencilValue(1)
//                    depthStencilClearValue

                )
            ),
            contents = VkSubpassContents.INLINE
        )
    }
    fun beginRenderPass_Full(cb: CommandBuffer, renderpass: VkRenderPass) {
        cb.beginRenderPass(
            renderPassBegin = RenderPassBeginInfo(
                renderPass = renderpass,
                framebuffer = framebuffer,
                renderArea = Rect2D(
                    offset = Offset2D(0, 0),
                    extent = Extent2D(createInfo.width, createInfo.height)
                ),
                clearValues = arrayOf(
                    ClearValue(0.0f, 0.0f, 0.0f, 1.0f),
                    ClearValue(1.0f, 0.0f, 0.0f, 0.0f)  //对应attachments
                )
            ),
            contents = VkSubpassContents.INLINE
        )
    }

    fun withRenderpass(cb: CommandBuffer, drawCmd: Recorder) {
        cb.begin(
            CommandBufferBeginInfo(
                flags = 0,
                inheritanceInfo = null
            )
        )
        beginRenderPass_Full(cb)

        drawCmd(cb)

        cb.endRenderPass()
        cb.end()
    }

    fun begin(cb: CommandBuffer) {
        cb.begin(
            CommandBufferBeginInfo(
                flags = 0,
                inheritanceInfo = null
            )
        )
        beginRenderPass_Full(cb)
    }
    fun end(cb: CommandBuffer) {
        cb.endRenderPass()
        cb.end()
    }

    fun withRenderpass(cb: CommandBuffer, drawCmd: Recorder, renderpass: VkRenderPass) {
        cb.begin(
            CommandBufferBeginInfo(
                flags = 0,
                inheritanceInfo = null
            )
        )
        beginRenderPass_Full(cb,renderpass)

        drawCmd(cb)

        cb.endRenderPass()
        cb.end()
    }





    fun destroy() {
        device.device.destroy(framebuffer)
    }

}