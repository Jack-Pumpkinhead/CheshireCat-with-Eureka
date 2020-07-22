package vulkan.util

import glm_.L
import glm_.mat4x4.Mat4
import kool.Stack
import kool.adr
import kool.remSize
import vkk.entities.VkDeviceSize
import vkk.memCopy
import vulkan.OzPhysicalDevice
import vulkan.OzVulkan
import vulkan.buffer.OzVMA
import vulkan.concurrent.SyncArray

/**
 * Created by CowardlyLion on 2020/6/15 22:26
 */
@Deprecated("ddd")
class DynamicMatrices(val vma: OzVMA, physicalDevice: OzPhysicalDevice) {

    val matrices = SyncArray<Mat4>()    //two matrices an object

    val minUboAlignment = physicalDevice.properties.limits.minUniformBufferOffsetAlignment.L

    var dynamicAlignment = Mat4.size.L * 2

    var buffer = vma.of_uniform(1)

    init {
        /*OzVulkan.logger.info {
            "predy: $dynamicAlignment"
        }*/
        if (minUboAlignment.L > 0)
            dynamicAlignment = (dynamicAlignment + minUboAlignment.L - 1) and (minUboAlignment.L - 1).inv()

        /*OzVulkan.logger.info {
            val minAlignment = physicalDevice.properties.limits.minUniformBufferOffsetAlignment.L
            val aaa = if (Mat4.size.rem(minAlignment) == 0L){
                Mat4.size
            } else ((Mat4.size / minAlignment) + 1) * minAlignment
            "aftDy: $dynamicAlignment" +
                    "\naaa $aaa"
        }*/
    }

    suspend fun flush() {
        matrices.withLock {mats->
            if (mats.isEmpty()) return@withLock

            buffer.destroy()
            buffer = vma.of_uniform(dynamicAlignment.toInt() * mats.size)

            Stack { stack ->
                var adr = buffer.memory.map()
                var i = 0
                while (i < mats.size) {
                    val model = mats[i].toFloatBuffer(stack)
                    memCopy(model.adr, adr, VkDeviceSize(model.remSize))

                    i++
                    val projection_view = mats[i].toFloatBuffer(stack)
                    memCopy(projection_view.adr, adr + Mat4.size, VkDeviceSize(projection_view.remSize))

                    i++
                    adr += dynamicAlignment
                }
            }
//            buffer.memory.flush()
        }

    }

    fun destroy() {
        buffer.destroy()
    }

}