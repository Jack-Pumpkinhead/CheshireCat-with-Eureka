package vulkan

import mu.KotlinLogging
import vkk.VkCommandBufferLevel
import vkk.VkPipelineBindPoint
import vkk.VkSubpassContents
import vkk.identifiers.CommandBuffer
import vkk.vk10.allocateCommandBuffers
import vkk.vk10.begin
import vkk.vk10.beginRenderPass
import vkk.vk10.freeCommandBuffers
import vkk.vk10.structs.*

class OzCommandBuffers(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val commandPool: OzCommandPool,
    val framebuffers: OzFramebuffers,
    val swapchain: OzSwapchain,
    val pipeline: OzGraphicPipeline,
    val renderPass: OzRenderPass
) {

    val logger = KotlinLogging.logger { }

    val commandbuffers : Array<CommandBuffer>

    init {

        commandbuffers = device.device.allocateCommandBuffers(
            allocateInfo = CommandBufferAllocateInfo(
                commandPool = commandPool.commandpool,
                level = VkCommandBufferLevel.PRIMARY,
                commandBufferCount = framebuffers.framebuffers.size
            )
        )

        commandbuffers.forEachIndexed { i,it->
            it.begin(
                CommandBufferBeginInfo(
                    flags = 0,
                    inheritanceInfo = null
                )
            )    //reset buffer

            it.beginRenderPass(
                renderPassBegin = RenderPassBeginInfo(
                    renderPass = renderPass.renderpass,
                    framebuffer = framebuffers.framebuffers[i],
                    renderArea = Rect2D(
                        offset = Offset2D(0, 0),
                        extent = swapchain.extent
                    ),
                    clearValues = arrayOf(ClearValue(0.0f, 0.0f, 0.0f, 1.0f))
                ),
                contents = VkSubpassContents.INLINE
            )
            it.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline.graphicsPipelines[0]
            )
            it.draw(
                vertexCount = 3,
                instanceCount = 1,
                firstVertex = 0,
                firstInstance = 0
            )
            it.endRenderPass()
            it.end()
        }

    }

    fun destroy() {
        device.device.freeCommandBuffers(commandPool.commandpool, commandbuffers)
    }

}