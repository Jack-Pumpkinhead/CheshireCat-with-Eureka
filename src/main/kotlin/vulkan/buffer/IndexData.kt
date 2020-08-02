package vulkan.buffer

import glm_.value
import kool.BYTES
import vkk.VkIndexType
import vkk.entities.VkDeviceSize
import vkk.identifiers.CommandBuffer
import vulkan.command.CopyBuffer
import vulkan.command.OzCommands

/**
 * Created by CowardlyLion on 2020/7/21 17:05
 */
class IndexData(
    val buffer: OzBuffer,
    val vma: OzVMA,
    val copyBuffer: CopyBuffer,
    var arr: IntArray,
    var indexBuffer: VmaBuffer,    //device local
    var isDynamic: Boolean
) {


    fun bind(cb: CommandBuffer) {
        cb.bindIndexBuffer(
            buffer = indexBuffer.vkBuffer,
            offset = VkDeviceSize(0),
            indexType = VkIndexType.UINT32
        )
    }
    fun draw(cb: CommandBuffer) {
//        val ints = indexBuffer.memory.bytes / Int.BYTES   //造成旧数据的绘制
        val ints = arr.size
        if (ints > 0) {
            cb.drawIndexed(
                indexCount = ints,
                instanceCount = 1,
                firstIndex = 0,
                vertexOffset = 0,
                firstInstance = 0
            )
        }
    }

    fun replaceBuffer(buffer: VmaBuffer) {
        val old = indexBuffer
        indexBuffer = buffer
        old.destroy()
    }

    suspend fun reload_deviceLocal() {
        if (isDynamic) {
            isDynamic = false
            replaceBuffer(buffer.indexBuffer_device_local(arr))
            return
        }

        if (arr.isEmpty()) {
            if (indexBuffer.memory.bytes > 1) {
                replaceBuffer(vma.of_IndexBuffer_device_local(1))
            }
            return
        }
        val bytes = arr.size * Int.BYTES
        val bufferSize = indexBuffer.memory.bytes
        if (bytes > bufferSize || bytes < bufferSize / 2) {
            replaceBuffer(buffer.indexBuffer_device_local(arr))
        } else {
            val staging = vma.createBuffer_indexStaging(bytes)
            staging.fill(arr)
            copyBuffer.copyBuffer(staging.pBuffer, indexBuffer.pBuffer, bytes)
            staging.destroy()
        }
    }

    //HOST_VISIBLE HOST_COHERENT
    fun reload_Dynamic() {
        if (!isDynamic) {
            isDynamic = true
            replaceBuffer(buffer.indexBuffer(arr))
            return
        }

        if (arr.isEmpty()) {
            if (indexBuffer.memory.bytes > 1) {
                replaceBuffer(vma.indexBuffer(1))
            }
            return
        }
        val bytes = arr.size * Int.BYTES
        val bufferSize = indexBuffer.memory.bytes
        if (bytes > bufferSize || bytes < bufferSize / 2) {
            replaceBuffer(buffer.indexBuffer(arr))
        } else {
            indexBuffer.fill(arr)
        }
    }

    fun destroy() {
        indexBuffer.destroy()
    }



}