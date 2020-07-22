package vulkan.buffer

import glm_.L
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kool.Stack
import kool.adr
import kool.remSize
import math.matrix.InArrModel
import vkk.entities.VkDeviceSize
import vkk.memCopy
import vkk.vk10.structs.DescriptorBufferInfo
import vulkan.OzPhysicalDevice
import vulkan.concurrent.SyncArray2

/**
 * Created by CowardlyLion on 2020/7/20 23:07
 */
class MatricesBuffer(val vma: OzVMA, physicalDevice: OzPhysicalDevice) {

    val matrices = SyncArray2<Mat4>()
    var buffer = vma.of_uniform(1)
    var bufferSize = 1  //in bytes

    val descriptorBI
        get() = DescriptorBufferInfo(
            buffer = buffer.vkBuffer,
            offset = VkDeviceSize(0),
            range = VkDeviceSize(bufferSize)
        )

    fun resizeBuffer(size: Int) {
        bufferSize = size
        buffer.destroy()
        buffer = vma.of_uniform(size)
    }

    val minUboAlignment = physicalDevice.properties.limits.minUniformBufferOffsetAlignment.L
    val dynamicAlignment = calcDynamicAlignment(Mat4.size.L, minUboAlignment).toInt()

    fun calcDynamicAlignment(alignment: Long, minUboAlignment: Long): Long =
        if (minUboAlignment.L > 0) {
            (alignment + minUboAlignment.L - 1) and (minUboAlignment.L - 1).inv()
        } else alignment

    suspend fun fetch(): SyncArray2<Mat4>.InArr {
        return matrices.assign(Mat4())
    }

    suspend fun fetchModel(
        pos: Vec3 = Vec3(),
        rot: Vec3 = Vec3(),
        scale: Vec3 = Vec3(1, 1, 1)
    ): InArrModel {
        return InArrModel(pos, rot, scale, matrices.assign(Mat4()))
    }


    suspend fun flush() {
        matrices.withLock { mats->
            if (mats.isEmpty()) {
                if (bufferSize > 1) {
                    resizeBuffer(1)
                }
                return@withLock
            }

            val size = dynamicAlignment * mats.size
            if (size > bufferSize || size < bufferSize / 2) {
                resizeBuffer(size)
            }

            var adr = buffer.memory.map()
            mats.forEach {
                Stack { stack ->
                    val floatBuffer = it.obj.toFloatBuffer(stack)
                    memCopy(floatBuffer.adr, adr, VkDeviceSize(floatBuffer.remSize))
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