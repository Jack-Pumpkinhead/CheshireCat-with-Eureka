package vulkan.util

import glm_.L
import glm_.mat4x4.Mat4
import kool.Stack
import kool.adr
import kool.remSize
import math.matrix.Model
import vkk.entities.VkDeviceSize
import vkk.memCopy
import vkk.vk10.structs.DescriptorBufferInfo
import vulkan.OzPhysicalDevice
import vulkan.buffer.OzVMA
import vulkan.concurrent.SyncArray

/**
 * Created by CowardlyLion on 2020/6/28 23:09
 */
class DynamicModel(val vma: OzVMA, physicalDevice: OzPhysicalDevice) {

    val matrices = SyncArray<Mat4>()

    val minUboAlignment = physicalDevice.properties.limits.minUniformBufferOffsetAlignment.L

    var dynamicAlignment = Mat4.size.L

    var buffer = vma.of_uniform(1)
    init {
        if (minUboAlignment.L > 0)
            dynamicAlignment = (dynamicAlignment + minUboAlignment.L - 1) and (minUboAlignment.L - 1).inv()

    }

    suspend fun fetch(): Model {
        val index = matrices.assign(Mat4())
        return Model(mats = matrices, index = index)
    }
    /*suspend fun fetch(mat: Mat4): Model {
        val index = matrices.assign(mat)
        return Model(mats = matrices, index = index)
    }*/


    suspend fun flush():Int {
        return matrices.withLockR {mats->
            if (mats.isEmpty()) return@withLockR 0

            buffer.destroy()
            buffer = vma.of_uniform(dynamicAlignment.toInt() * mats.size)

            var adr = buffer.memory.map()
            mats.forEach {
                Stack { stack ->
                    val model = it.toFloatBuffer(stack)
                    memCopy(model.adr, adr, VkDeviceSize(model.remSize))
                    adr += dynamicAlignment
                }
            }
            /*Stack { stack ->
                var adr = buffer.memory.map()
                mats.forEach {
                    val model = it.toFloatBuffer(stack)
                    memCopy(model.adr, adr, VkDeviceSize(model.remSize))
                    adr += dynamicAlignment

                }
            }
            */

//            buffer.memory.flush()
            mats.size
        }

    }


    fun destroy() {
        buffer.destroy()
    }
}