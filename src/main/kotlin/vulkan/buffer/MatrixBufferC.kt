package vulkan.buffer

import glm_.mat4x4.Mat4
import kool.BYTES
import kool.Stack
import kool.adr
import kool.remSize
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vkk.entities.VkDeviceSize
import vkk.memCopy
import vkk.vk10.structs.DescriptorBufferInfo

/**
 * Created by CowardlyLion on 2020/6/28 23:18
 */
class MatrixBufferC(val vma: OzVMA) {
    var mat4 = Mat4()
    val bytes = 4 * 4 * Float.BYTES
    val buffer = vma.of_uniform(bytes)

    val mutex = Mutex()
    suspend fun lockOut(action: suspend (MatrixBufferC) -> Unit) {
        mutex.withLock {
            action(this)
        }
    }


    suspend fun flush() {
        Stack { stack ->
            val floatBuffer = mutex.withLock {
                mat4.toFloatBuffer(stack)
            }
            memCopy(floatBuffer.adr, buffer.memory.map(), VkDeviceSize(floatBuffer.remSize))
        }
//           buffer.memory.flush()
    }

    fun descriptorBI() = DescriptorBufferInfo(
        buffer = buffer.vkBuffer,
        offset = VkDeviceSize(0),
        range = VkDeviceSize(bytes)
    )

    fun destroy() {
        buffer.destroy()
    }

}