package vulkan.buffer

import glm_.mat4x4.Mat4
import kool.BYTES
import kool.Stack
import kool.adr
import kool.remSize
import vkk.entities.VkDeviceSize
import vkk.memCopy

/**
 * Created by CowardlyLion on 2020/5/30 20:59
 */
class MatrixBuffer(val vma: OzVMA) {

    val mat4 = Mat4()
    val bytes = 4 * 4 * Float.BYTES
    val buffer = vma.of_uniform(bytes)

    fun flush() {
        Stack { stack ->
            val floatBuffer = mat4.toFloatBuffer(stack)
            memCopy(floatBuffer.adr, buffer.memory.map(), VkDeviceSize(floatBuffer.remSize))
        }
//           buffer.memory.flush()
    }

    fun destroy() {
        buffer.destroy()
    }

}