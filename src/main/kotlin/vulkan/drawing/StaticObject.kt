package vulkan.drawing

import vkk.VkIndexType
import vkk.entities.VkBuffer_Array
import vkk.entities.VkDeviceSize
import vkk.entities.VkDeviceSize_Array
import vkk.identifiers.CommandBuffer
import vkk.vk10.bindVertexBuffers
import vulkan.buffer.VmaBuffer

/**
 * Created by CowardlyLion on 2020/7/15 16:37
 */
class StaticObject(
    val vertexBuffer: VmaBuffer,
    val indexBuffer: VmaBuffer,
    val indexCount: Int
) {

    fun bind(cb: CommandBuffer) {
        cb.bindVertexBuffers(
            firstBinding = 0,
            bindingCount = 1,
            buffers = VkBuffer_Array(listOf(vertexBuffer.vkBuffer)),
            offsets = VkDeviceSize_Array(listOf(VkDeviceSize(0)))
        )
        cb.bindIndexBuffer(
            buffer = indexBuffer.vkBuffer,
            offset = VkDeviceSize(0),
            indexType = VkIndexType.UINT32
        )
    }

    fun draw(cb: CommandBuffer) {
        cb.drawIndexed(
            indexCount = indexCount,
            instanceCount = 1,
            firstIndex = 0,
            vertexOffset = 0,
            firstInstance = 0
        )
    }



    fun destroy() {
        vertexBuffer.destroy()
        indexBuffer.destroy()
    }


}