package vulkan.concurrent

import glm_.mat4x4.Mat4
import kool.BYTES
import kool.Stack
import kool.adr
import kool.remSize
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vkk.entities.VkDeviceSize
import vkk.memCopy
import vulkan.OzPhysicalDevice
import vulkan.OzVulkan
import vulkan.buffer.OzVMA

/**
 * Created by CowardlyLion on 2020/5/10 20:22
 */
class OzMatrixBuffer(val vma: OzVMA, val physicalDevice: OzPhysicalDevice) {


    //use set(i,m) to assure concurrency
    val matrices = mutableListOf<Mat4>()

    val mutex = Mutex()

    suspend fun allocate(mat: Mat4): Int {
        return mutex.withLock {
            matrices += mat
            matrices.lastIndex
        }
    }
    suspend fun resize(size: Int) {
        mutex.withLock {
            if (matrices.size < size) {
                repeat(size - matrices.size) {
                    matrices.add(Mat4.identity)
                }
            } else {
                repeat(matrices.size - size) {
                    matrices.removeAt(matrices.lastIndex)
                }
            }
        }
    }


    val minAlignment = physicalDevice.properties.limits.minUniformBufferOffsetAlignment.L

    val alignment = calcSize(4 * 4 * Float.BYTES.toLong()).toInt()

    fun calcSize(matBytes: Long): Long {
        return if (matBytes.rem(minAlignment) == 0L){
            matBytes
        } else ((matBytes / minAlignment) + 1) * minAlignment
    }

    var size = matrices.size
    var bytes = alignment * size
    var buffer = vma.of_uniform_mf(1)   //initial nonzero to prevent null pointer exception

    suspend fun set(index:Int,mat:Mat4) = mutex.withLock {
        matrices[index] = mat
    }

    suspend fun flush() {
        mutex.withLock {
            if (matrices.size > size) {
                size = matrices.size
                bytes = alignment * size
                buffer.destroy()
                buffer = vma.of_uniform_mf(bytes)
            }
            Stack { stack ->
                var adr = buffer.map()
                matrices.forEach {
                    val floatBuffer = it.toFloatBuffer(stack)
                    memCopy(floatBuffer.adr, adr, VkDeviceSize(floatBuffer.remSize))
                    adr += alignment
                }
            }
            /*OzVulkan.logger.info {
                matrices.size
            }*/
            buffer.flush()
        }

    }

    fun destroy() {
        buffer.destroy()
    }

}