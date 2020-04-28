package vulkan.drawing

import kool.Stack
import kool.adr
import kool.remSize
import mu.KotlinLogging
import vkk.entities.VkDeviceSize
import vkk.memCopy
import vulkan.OzCommandPool
import java.nio.FloatBuffer
import java.nio.IntBuffer

class OzVertexDataImmutable(vma: OzVMA, val commandPool: OzCommandPool) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    lateinit var vertices: FloatBuffer

    lateinit var indices: IntBuffer


    val vertexBuffer: VMABuffer
    val vertexBuffer_device_local: VMABuffer

    val indexBuffer: VMABuffer
    val indexBuffer_device_local: VMABuffer

    init {
        Stack {
            vertices = it.floats(
                -0.5f, -0.5f, +0f, 1f, 0f, 0f,
                +0.5f, -0.5f, +0f, 0f, 1f, 0f,
                +0.5f, +0.5f, +0f, 0f, 0f, 1f,
                -0.5f, +0.5f, +0f, 1f, 1f, 1f
            )
            indices = it.ints(
                0, 1, 2, 2, 3, 0
            )
        }

        vertexBuffer = vma.of_staging(vertices.remSize)
        indexBuffer = vma.of_staging(indices.remSize)

        vertexBuffer.fill(vertices)
        indexBuffer.fill(indices)

        vertexBuffer_device_local = vma.of_VertexBuffer_device_local(vertices.remSize)
        indexBuffer_device_local = vma.of_IndexBuffer_device_local(indices.remSize)
    }

    fun copyBufferToDeviceLocal() {
        commandPool.copyBuffer(vertexBuffer.pBuffer, vertexBuffer_device_local.pBuffer, vertices.remSize)
        commandPool.copyBuffer(vertexBuffer.pBuffer, vertexBuffer_device_local.pBuffer, vertices.remSize)
    }




    fun destroy() {
        vertexBuffer.destroy()
        vertexBuffer_device_local.destroy()
        indexBuffer.destroy()
        indexBuffer_device_local.destroy()
    }
}