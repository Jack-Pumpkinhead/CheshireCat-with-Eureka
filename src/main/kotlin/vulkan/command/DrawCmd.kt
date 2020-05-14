package vulkan.command

import vkk.VkIndexType
import vkk.VkPipelineBindPoint
import vkk.VkSubpassContents
import vkk.entities.*
import vkk.identifiers.CommandBuffer
import vkk.vk10.begin
import vkk.vk10.beginRenderPass
import vkk.vk10.bindDescriptorSets
import vkk.vk10.bindVertexBuffers
import vkk.vk10.structs.*
import vulkan.OzFramebuffer

/**
 * Created by CowardlyLion on 2020/5/9 16:28
 */
class DrawCmd {

    companion object {
        fun recordDrawIndexed(
            cb: CommandBuffer,
            renderPass: VkRenderPass,
            framebuffer: VkFramebuffer,
            extent2D: Extent2D,
            pipeline: VkPipeline,
            buffer_array: VkBuffer_Array,
            offset_array: VkDeviceSize_Array,
            indexBuffer: VkBuffer,
            count: Int
        ): CommandBuffer {
            cb.begin(
                CommandBufferBeginInfo(
                    flags = 0,
                    inheritanceInfo = null
                )
            )
            cb.beginRenderPass(
                renderPassBegin = RenderPassBeginInfo(
                    renderPass = renderPass,
                    framebuffer = framebuffer,
                    renderArea = Rect2D(
                        offset = Offset2D(0, 0),
                        extent = extent2D
                    ),
                    clearValues = arrayOf(ClearValue(0.0f, 0.0f, 0.0f, 1.0f))
//                    clearValues = arrayOf(ClearValue(0.01f*ii, 0.3f, 0.2f, 1.0f))
                ),
                contents = VkSubpassContents.INLINE
            )
            cb.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline
            )
            cb.bindVertexBuffers(
                firstBinding = 0,
                bindingCount = buffer_array.size,
                buffers = buffer_array,
                offsets = offset_array
            )

            cb.bindIndexBuffer(
                buffer = indexBuffer,
                offset = VkDeviceSize(0),
                indexType = VkIndexType.UINT32
            )
            cb.drawIndexed(
                indexCount = count,
                instanceCount = 1,
                firstIndex = 0,
                vertexOffset = 0,
                firstInstance = 0
            )

            cb.endRenderPass()
            cb.end()
            return cb
        }

        fun recordDrawUniform(
            cb: CommandBuffer,
            renderPass: VkRenderPass,
            framebuffer: VkFramebuffer,
            extent2D: Extent2D,
            pipeline: VkPipeline,
            pipelineLayout: VkPipelineLayout,
            descriptorSets: VkDescriptorSet_Array,
            buffer_array: VkBuffer_Array,
            offset_array: VkDeviceSize_Array,
            indexBuffer: VkBuffer,
            count: Int
        ): CommandBuffer {
            cb.begin(
                CommandBufferBeginInfo(
                    flags = 0,
                    inheritanceInfo = null
                )
            )
            cb.beginRenderPass(
                renderPassBegin = RenderPassBeginInfo(
                    renderPass = renderPass,
                    framebuffer = framebuffer,
                    renderArea = Rect2D(
                        offset = Offset2D(0, 0),
                        extent = extent2D
                    ),
                    clearValues = arrayOf(ClearValue(0.0f, 0.0f, 0.0f, 1.0f))
//                    clearValues = arrayOf(ClearValue(0.01f*ii, 0.3f, 0.2f, 1.0f))
                ),
                contents = VkSubpassContents.INLINE
            )

//            cb.pushConstants()
//            cb.nextSubpass(VkSubpassContents.INLINE)

            cb.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline
            )
            cb.bindVertexBuffers(
                firstBinding = 0,
                bindingCount = buffer_array.size,
                buffers = buffer_array,
                offsets = offset_array
            )

            cb.bindIndexBuffer(
                buffer = indexBuffer,
                offset = VkDeviceSize(0),
                indexType = VkIndexType.UINT32
            )

            cb.bindDescriptorSets(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                layout = pipelineLayout,
                firstSet = 0,
                descriptorSets = descriptorSets,
                dynamicOffsets = intArrayOf()   //one per dynamic descriptorSet
            )

            cb.drawIndexed(
                indexCount = count,
                instanceCount = 1,
                firstIndex = 0,
                vertexOffset = 0,
                firstInstance = 0
            )

            cb.endRenderPass()
            cb.end()
            return cb
        }
        fun recordDrawUniformDynamic(
            cb: CommandBuffer,
            renderPass: VkRenderPass,
            framebuffer: VkFramebuffer,
            extent2D: Extent2D,
            pipeline: VkPipeline,
            pipelineLayout: VkPipelineLayout,
            descriptorSets: VkDescriptorSet_Array,
            dynamicOffsets: IntArray,
            buffer_array: VkBuffer_Array,
            offset_array: VkDeviceSize_Array,
            indexBuffer: VkBuffer,
            count: Int
        ): CommandBuffer {
            cb.begin(
                CommandBufferBeginInfo(
                    flags = 0,
                    inheritanceInfo = null
                )
            )
            cb.beginRenderPass(
                renderPassBegin = RenderPassBeginInfo(
                    renderPass = renderPass,
                    framebuffer = framebuffer,
                    renderArea = Rect2D(
                        offset = Offset2D(0, 0),
                        extent = extent2D
                    ),
                    clearValues = arrayOf(ClearValue(0.0f, 0.0f, 0.0f, 1.0f))
//                    clearValues = arrayOf(ClearValue(0.01f*ii, 0.3f, 0.2f, 1.0f))
                ),
                contents = VkSubpassContents.INLINE
            )

//            cb.pushConstants()
//            cb.nextSubpass(VkSubpassContents.INLINE)

            cb.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline
            )
            cb.bindVertexBuffers(
                firstBinding = 0,
                bindingCount = buffer_array.size,
                buffers = buffer_array,
                offsets = offset_array
            )

            cb.bindIndexBuffer(
                buffer = indexBuffer,
                offset = VkDeviceSize(0),
                indexType = VkIndexType.UINT32
            )

            cb.bindDescriptorSets(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                layout = pipelineLayout,
                firstSet = 0,
                descriptorSets = descriptorSets,
                dynamicOffsets = dynamicOffsets   //one per dynamic descriptorSet
            )

            cb.drawIndexed(
                indexCount = count,
                instanceCount = 1,
                firstIndex = 0,
                vertexOffset = 0,
                firstInstance = 0
            )

            cb.endRenderPass()
            cb.end()
            return cb
        }

        fun recordDrawUniformDynamic(
            cb: CommandBuffer,
            framebuffer: OzFramebuffer,
            pipeline: VkPipeline,
            pipelineLayout: VkPipelineLayout,
            descriptorSets: VkDescriptorSet_Array,
            dynamicOffsets: IntArray,
            buffer_array: VkBuffer_Array,
            offset_array: VkDeviceSize_Array,
            indexBuffer: VkBuffer,
            count: Int
        ): CommandBuffer {
            cb.begin(
                CommandBufferBeginInfo(
                    flags = 0,
                    inheritanceInfo = null
                )
            )
            framebuffer.beginRenderPass_Full(cb)

//            cb.pushConstants()
//            cb.nextSubpass(VkSubpassContents.INLINE)

            cb.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline
            )
            cb.bindVertexBuffers(
                firstBinding = 0,
                bindingCount = buffer_array.size,
                buffers = buffer_array,
                offsets = offset_array
            )

            cb.bindIndexBuffer(
                buffer = indexBuffer,
                offset = VkDeviceSize(0),
                indexType = VkIndexType.UINT32
            )

            cb.bindDescriptorSets(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                layout = pipelineLayout,
                firstSet = 0,
                descriptorSets = descriptorSets,
                dynamicOffsets = dynamicOffsets   //one per dynamic descriptorSet
            )

            cb.drawIndexed(
                indexCount = count,
                instanceCount = 1,
                firstIndex = 0,
                vertexOffset = 0,
                firstInstance = 0
            )

            cb.endRenderPass()
            cb.end()
            return cb
        }

    }

}