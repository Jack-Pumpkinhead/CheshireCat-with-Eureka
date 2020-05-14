package vulkan.command

import kool.Ptr
import kotlinx.coroutines.CancellationException
import vkk.VkCommandBufferUsage
import vkk.VkResult
import vkk.entities.VkBuffer
import vkk.entities.VkDeviceSize
import vkk.identifiers.CommandBuffer
import vkk.vk10.begin
import vkk.vk10.copyBuffer
import vkk.vk10.structs.BufferCopy
import vkk.vk10.structs.CommandBufferBeginInfo
import vkk.vk10.structs.SubmitInfo
import vulkan.OzCommandPools
import vulkan.OzQueues
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/5/9 15:59
 */
class CopyBuffer(val commandPools: OzCommandPools, val queues: OzQueues) {

    companion object {
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
    }

    /**
     * allocate and submit
     * @throws CancellationException
     */
    suspend fun copyBuffer(src: Ptr, dst: Ptr, size: Int) {

        val cb = commandPools.transferCP.allocate().await()

        recordCopyBuffer(VkBuffer(src), VkBuffer(dst), size, cb)

        val submitInfo = SubmitInfo(
            commandBuffers = arrayOf(cb)
        )
        val result = queues.transferQ.submit(submitInfo).await()

        if (result != VkResult.SUCCESS) {
            OzVulkan.logger.warn {
                "copy buffer failed, src: $src, dst: $dst, size: $size "
            }
        }

        commandPools.transferCP.free(cb)
    }

}