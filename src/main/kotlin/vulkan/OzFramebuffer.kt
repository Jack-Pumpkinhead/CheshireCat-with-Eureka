package vulkan

import vkk.VkSubpassContents
import vkk.entities.*
import vkk.identifiers.CommandBuffer
import vkk.vk10.beginRenderPass
import vkk.vk10.createFramebuffer
import vkk.vk10.structs.*
import vulkan.OzDevice

class OzFramebuffer(
    val device: OzDevice,
    val createInfo: FramebufferCreateInfo
) {

    val framebuffer: VkFramebuffer = device.device.createFramebuffer(createInfo)




    //drawing on full size
    fun beginRenderPass_Full(cb: CommandBuffer) {
        cb.beginRenderPass(
            renderPassBegin = RenderPassBeginInfo(
                renderPass = createInfo.renderPass,
                framebuffer = framebuffer,
                renderArea = Rect2D(
                    offset = Offset2D(0, 0),
                    extent = Extent2D(createInfo.width, createInfo.height)
                ),
                clearValues = arrayOf(ClearValue(0.0f, 0.0f, 0.0f, 1.0f))
            ),
            contents = VkSubpassContents.INLINE
        )
    }




    fun destroy() {
        device.device.destroy(framebuffer)
    }

}