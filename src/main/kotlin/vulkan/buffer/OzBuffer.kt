package vulkan.buffer

import game.main.Univ
import kool.BYTES
import kool.Stack
import kool.free
import org.lwjgl.system.MemoryUtil
import vulkan.command.CopyBuffer
import vulkan.drawing.StaticObject

/**
 * Created by CowardlyLion on 2020/7/15 16:59
 */
class OzBuffer(val vma: OzVMA, val copyBuffer: CopyBuffer) {

    suspend fun vertexBuffer_device_local(arr: FloatArray): VmaBuffer {
        val bytes = arr.size * Float.BYTES
        val staging = vma.createBuffer_vertexStaging(bytes)

        Univ.logger.info {
            "vertex data size: ${arr.size}"
        }
        if (arr.size < 10000) {
            Stack {
                staging.memory.fill(
                    it.mallocFloat(arr.size).put(arr).flip()
                )
            }
        } else {
            val buffer = MemoryUtil.memAllocFloat(arr.size)
            staging.memory.fill(
                buffer.put(arr).flip()
            )
            buffer.free()
        }

        val deviceLocal = vma.of_VertexBuffer_device_local(bytes)
        copyBuffer.copyBuffer(staging.pBuffer, deviceLocal.pBuffer, bytes)
        staging.destroy()
        return deviceLocal
    }

    suspend fun indexBuffer_device_local(arr: IntArray): VmaBuffer {
        val bytes = arr.size * Int.BYTES
        val staging = vma.createBuffer_indexStaging(bytes)

        Univ.logger.info {
            "index data size: ${arr.size}"
        }
        if (arr.size < 10000) {
            Stack {
                staging.memory.fill(
                    it.mallocInt(arr.size).put(arr).flip()
                )
            }
        } else {
            val buffer = MemoryUtil.memAllocInt(arr.size)
            staging.memory.fill(
                buffer.put(arr).flip()
            )
            buffer.free()
        }
        val deviceLocal = vma.of_IndexBuffer_device_local(bytes)
        copyBuffer.copyBuffer(staging.pBuffer, deviceLocal.pBuffer, bytes)
        staging.destroy()
        return deviceLocal
    }

    suspend fun staticObject_deviceLocal(vertexBuffer: FloatArray, indices: IntArray): StaticObject {
        return StaticObject(
            vertexBuffer = vertexBuffer_device_local(vertexBuffer),
            indexBuffer = indexBuffer_device_local(indices),
            indexCount = indices.size
        )
    }


}