package vulkan.buffer

import kool.BYTES
import vkk.entities.VkBuffer_Array
import vkk.entities.VkDeviceSize
import vkk.entities.VkDeviceSize_Array
import vkk.identifiers.CommandBuffer
import vkk.vk10.bindVertexBuffers
import vulkan.command.OzCommands

/**
 * Created by CowardlyLion on 2020/7/21 16:39
 */
class VertexData(
    val buffer: OzBuffer,
    val vma: OzVMA,
    val commands: OzCommands,
    var arr: FloatArray,
    var vertexBuffer: VmaBuffer,
    var bufferSize: Int,
    var isDynamic: Boolean
) {
    fun bind(cb: CommandBuffer) {
        cb.bindVertexBuffers(
            firstBinding = 0,
            bindingCount = 1,
            buffers = VkBuffer_Array(listOf(vertexBuffer.vkBuffer)),
            offsets = VkDeviceSize_Array(listOf(VkDeviceSize(0)))
        )
    }
    fun replaceBuffer(buffer: VmaBuffer, size: Int) {
        bufferSize = size
        val old = vertexBuffer
        vertexBuffer = buffer
        old.destroy()
    }

    //device local
    suspend fun reload_deviceLocal() {
        if (isDynamic) {
            isDynamic = false
            val bytes = arr.size * Float.BYTES
            replaceBuffer(buffer.vertexBuffer_device_local(arr), bytes)
            return
        }
        if (arr.isEmpty()) {
            if (bufferSize > 1) {
                replaceBuffer(vma.of_VertexBuffer_device_local(1), 1)
            }
            return
        }
        val bytes = arr.size * Float.BYTES
        if (bytes > bufferSize || bytes < bufferSize / 2) {
            replaceBuffer(buffer.vertexBuffer_device_local(arr), bytes)
        } else {
            val staging = vma.createBuffer_vertexStaging(bytes)
            staging.fill(arr)
            commands.copyBuffer.copyBuffer(staging.pBuffer, vertexBuffer.pBuffer, bytes)
            staging.destroy()
        }
    }


    //HOST_VISIBLE HOST_COHERENT
    fun reload_Dynamic() {
        if (!isDynamic) {
            isDynamic = true
            val bytes = arr.size * Float.BYTES
            replaceBuffer(buffer.vertexBuffer(arr), bytes)
            return
        }

        if (arr.isEmpty()) {
            if (bufferSize > 1) {
                replaceBuffer(vma.vertexBuffer(1), 1)
            }
            return
        }
        val bytes = arr.size * Float.BYTES
        if (bytes > bufferSize || bytes < bufferSize / 2) {
            replaceBuffer(buffer.vertexBuffer(arr), bytes)
        } else {
            vertexBuffer.fill(arr)
        }
    }

    fun destroy() {
        vertexBuffer.destroy()
    }



}