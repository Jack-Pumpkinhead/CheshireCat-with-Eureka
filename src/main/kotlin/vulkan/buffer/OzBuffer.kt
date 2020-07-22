package vulkan.buffer

import kool.BYTES
import vulkan.command.CopyBuffer
import vulkan.drawing.StaticObject

/**
 * Created by CowardlyLion on 2020/7/15 16:59
 */
class OzBuffer(val vma: OzVMA, val copyBuffer: CopyBuffer) {

    suspend fun vertexBuffer_device_local(arr: FloatArray): VmaBuffer {
        val bytes = arr.size * Float.BYTES
        val staging = vma.createBuffer_vertexStaging(bytes)

//        Univ.logger.info {
//            "vertex data size: ${arr.size}"
//        }
        staging.fill(arr)

        val deviceLocal = vma.of_VertexBuffer_device_local(bytes)
        copyBuffer.copyBuffer(staging.pBuffer, deviceLocal.pBuffer, bytes)
        staging.destroy()
        return deviceLocal
    }
    fun vertexBuffer(arr: FloatArray): VmaBuffer {
        val bytes = arr.size * Float.BYTES
        val buffer = vma.vertexBuffer(bytes)
        buffer.fill(arr)
        return buffer
    }


    suspend fun indexBuffer_device_local(arr: IntArray): VmaBuffer {
        val bytes = arr.size * Int.BYTES
        val staging = vma.createBuffer_indexStaging(bytes)

//        Univ.logger.info {
//            "index data size: ${arr.size}"
//        }
        staging.fill(arr)
        val deviceLocal = vma.of_IndexBuffer_device_local(bytes)
        copyBuffer.copyBuffer(staging.pBuffer, deviceLocal.pBuffer, bytes)
        staging.destroy()
        return deviceLocal
    }

    fun indexBuffer(arr: IntArray): VmaBuffer {
        val bytes = arr.size * Int.BYTES
        val buffer = vma.indexBuffer(bytes)
        buffer.fill(arr)
        return buffer
    }


    suspend fun staticObject_deviceLocal(vertexBuffer: FloatArray, indices: IntArray): StaticObject {
        return StaticObject(
            vertexBuffer = vertexBuffer_device_local(vertexBuffer),
            indexBuffer = indexBuffer_device_local(indices),
            indexCount = indices.size
        )
    }


}