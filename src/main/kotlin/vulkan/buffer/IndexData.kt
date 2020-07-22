package vulkan.buffer

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
    var bufferSize: Int,
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
        if (bufferSize > 1) {
            cb.drawIndexed(
                indexCount = bufferSize / Int.BYTES,
                instanceCount = 1,
                firstIndex = 0,
                vertexOffset = 0,
                firstInstance = 0
            )
        }
    }

    fun replaceBuffer(buffer: VmaBuffer, size: Int) {
        bufferSize = size
        val old = indexBuffer
        indexBuffer = buffer
        old.destroy()
    }

    suspend fun reload_deviceLocal() {
        if (isDynamic) {
            isDynamic = false
            val bytes = arr.size * Int.BYTES
            replaceBuffer(buffer.indexBuffer_device_local(arr), bytes)
            return
        }

        if (arr.isEmpty()) {
            if (bufferSize > 1) {
                replaceBuffer(vma.of_IndexBuffer_device_local(1), 1)
            }
            return
        }
        val bytes = arr.size * Int.BYTES
        if (bytes > bufferSize || bytes < bufferSize / 2) {
            replaceBuffer(buffer.indexBuffer_device_local(arr), bytes)
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
            val bytes = arr.size * Int.BYTES
            replaceBuffer(buffer.indexBuffer(arr), bytes)
            return
        }

        if (arr.isEmpty()) {
            if (bufferSize > 1) {
                replaceBuffer(vma.indexBuffer(1), 1)
            }
            return
        }
        val bytes = arr.size * Int.BYTES
        if (bytes > bufferSize || bytes < bufferSize / 2) {
            replaceBuffer(buffer.indexBuffer(arr), bytes)
        } else {
            indexBuffer.fill(arr)
        }
    }

    fun destroy() {
        indexBuffer.destroy()
    }



}