package vulkan.command

import mu.KotlinLogging
import vkk.*
import vkk.entities.*
import vkk.identifiers.CommandBuffer
import vkk.vk10.begin
import vkk.vk10.beginRenderPass
import vkk.vk10.bindVertexBuffers
import vkk.vk10.copyBuffer
import vkk.vk10.structs.*
import vulkan.OzCommandPools
import vulkan.OzDevice
import vulkan.OzVulkan
import vulkan.concurrent.OzCommandPool
import vulkan.concurrent.OzQueue

class OzCB(val ozVulkan: OzVulkan, val commandPools: OzCommandPools, val device: OzDevice) {

    companion object {

        val logger = KotlinLogging.logger { }

        fun recordCopyBuffer(src: VkBuffer, dst: VkBuffer, size: Int, cb: CommandBuffer): CommandBuffer {
            cb.begin(
                CommandBufferBeginInfo(
                    flags = VkCommandBufferUsage.ONE_TIME_SUBMIT_BIT.i
                )
            )
            val bufferCopy = BufferCopy(
                srcOffset = VkDeviceSize(0),
                dstOffset = VkDeviceSize(0),
                size = VkDeviceSize(size)
            )
            cb.copyBuffer(
                srcBuffer = src,
                dstBuffer = dst,
                regions = arrayOf(bufferCopy)
            )
            cb.end()
            return cb
        }

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
                offset = VkDeviceSize(0),   //what is offset?
                indexType = VkIndexType.UINT32
//                indexType = VkIndexType.UINT16
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

        /**
         * allocate and submit
         */
        suspend fun copyBuffer(src: VkBuffer, dst: VkBuffer, size: Int, transferCP: OzCommandPool, transferQ: OzQueue) {

            val cb = transferCP.allocate().await()
            recordCopyBuffer(src, dst, size, cb)

            val submitInfo = SubmitInfo(
                commandBuffers = arrayOf(cb)
            )

            transferQ.submit(submitInfo).await().also {
                if (it != VkResult.SUCCESS) {
                    logger.warn {
                        "copybuffer failed, src: ${src.hexString}, dst: ${dst.hexString}, size: $size "
                    }
                }
            }

            transferCP.free(cb)
        }

    }

    suspend fun copyBuffer(src: Long, dst: Long, size: Int) =
        copyBuffer(VkBuffer(src), VkBuffer(dst), size)

    suspend fun copyBuffer(src: VkBuffer, dst: VkBuffer, size: Int) =
        copyBuffer(src, dst, size, commandPools.transferCP, device.transferQ)


}