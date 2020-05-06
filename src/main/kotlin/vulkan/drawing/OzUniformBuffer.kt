package vulkan.drawing

import glm_.L
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kool.Stack
import kool.adr
import kool.remSize
import mu.KotlinLogging
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.vma.Vma
import vkk.VkBufferUsage
import vkk.VkMemoryProperty
import vkk.entities.VkDeviceSize
import vkk.memCopy
import vkk.vk10.structs.Extent2D
import vulkan.OzDevice
import vulkan.OzVulkan
import kotlin.time.TimeSource
import kotlin.time.measureTime

/**
 * Created by CowardlyLion on 2020/5/5 13:04
 */
class OzUniformBuffer(ozVulkan: OzVulkan, val device: OzDevice, val vma: OzVMA, val count: Int) {


    companion object {

        val logger = KotlinLogging.logger { }


    }

    //need to recreate when swapchain recreated //maybe not
    val buffers = List<VMABuffer>(count){ of(it.L)}

    fun of(bytes: Long) = vma.create(
        bytes = (4 * 4 * 3 * Long.SIZE_BYTES).L,
        bufferUsage = VkBufferUsage.UNIFORM_BUFFER_BIT.i,
        memoryProperty = VkMemoryProperty.HOST_VISIBLE_BIT.i,
        memoryProperty_prefered = VkMemoryProperty.HOST_COHERENT_BIT.i,
        vmaMemoryUsage = Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
    )

    var time = System.currentTimeMillis()

    fun update(imageIndex: Int, extent2D: Extent2D) {
        val current = System.currentTimeMillis()
        val sec = (current - time) / 1000.toFloat()
        val model = glm.rotate(Mat4(), sec * glm.radians(90.0f), Vec3(0.0f, 0.0f, 1.0f))
        val view = glm.lookAt(Vec3(2.0f, 2.0f, 2.0f), Vec3(0.0f, 0.0f, 0.0f), Vec3(0.0f, 0.0f, 1.0f))
//        val projection = glm.perspective(glm.radians(45.0f), extent2D.width / extent2D.height.toFloat(), 0.1f, 10.0f)
        val projection = glm.perspective(glm.radians(45.0f), extent2D.width / extent2D.height.toFloat(), 0.1f, 10.0f)
        projection[1][1] *= -1  //GLM was originally designed for OpenGL, where the Y coordinate of the clip coordinates is inverted.


        Stack { stack ->

            buffers[imageIndex].withMap {
                var adr = it
                val modelB = model.toFloatBuffer(stack)
                memCopy(modelB.adr, adr, VkDeviceSize(modelB.remSize))
                adr+=modelB.remSize

                val viewB = view.toFloatBuffer(stack)
                memCopy(viewB.adr, adr, VkDeviceSize(viewB.remSize))
                adr += viewB.remSize

                val projectionB = projection.toFloatBuffer(stack)
                memCopy(projectionB.adr, adr, VkDeviceSize(projectionB.remSize))

            }
        }


    }

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(vma::destroy, this::destroy)
    }

    fun destroy() {
        buffers.forEach {
            it.destroy()
        }
    }

}