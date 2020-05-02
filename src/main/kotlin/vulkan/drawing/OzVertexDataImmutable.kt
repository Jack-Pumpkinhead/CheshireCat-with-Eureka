package vulkan.drawing

import kool.*
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import vkk.VkPipelineBindPoint
import vkk.VkSubpassContents
import vkk.entities.VkBuffer
import vkk.entities.VkBuffer_Array
import vkk.entities.VkDeviceSize
import vkk.identifiers.CommandBuffer
import vkk.memCopy
import vkk.vk10.begin
import vkk.vk10.beginRenderPass
import vkk.vk10.bindVertexBuffers
import vkk.vk10.structs.*
import vulkan.command.OzCB
import vulkan.concurrent.OzFramebuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class OzVertexDataImmutable(
    vma: OzVMA,
    val ozcb: OzCB,
    val vertices: FloatArray,
    val indices: IntArray
) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val vbytes = vertices.size * Float.BYTES
    val ibytes = indices.size * Int.BYTES


    val vertexBuffer_device_local: VMABuffer
    val indexBuffer_device_local: VMABuffer

    var drawCmd: List<CommandBuffer>





    init {
//        logger.info {
//            "v: ${vertices.rem}  remsize: ${vertices.remSize}\t i: ${indices.rem}  remsize: ${indices.remSize}\t "
//        }

        val vertexBuffer = vma.of_staging_vertex(vbytes)
        val indexBuffer = vma.of_staging_index(ibytes)

        Stack {
            vertexBuffer.fill(
                it.mallocFloat(vertices.size).put(vertices).flip()
            )
            indexBuffer.fill(
                it.mallocInt(indices.size).put(indices).flip()
            )
        }


        vertexBuffer_device_local = vma.of_VertexBuffer_device_local(vbytes)
        indexBuffer_device_local = vma.of_IndexBuffer_device_local(ibytes)

        runBlocking {
            ozcb.copyBuffer(vertexBuffer.pBuffer, vertexBuffer_device_local.pBuffer, vbytes)
            ozcb.copyBuffer(indexBuffer.pBuffer, indexBuffer_device_local.pBuffer, ibytes)
        }
        vertexBuffer.destroy()
        indexBuffer.destroy()

        drawCmd = runBlocking { getCmd() }
        runBlocking {
            register()
        }
    }

    suspend fun getCmd(): List<CommandBuffer> {

        return ozcb.ozVulkan.framebuffer.fbs.map {
            val cb = ozcb.commandPools.graphicCP.allocate().await()

            it.recordDraw(
                cb,
                buffers = VkBuffer_Array(1) { VkBuffer(vertexBuffer_device_local.pBuffer) },
                indexBuffer = VkBuffer(indexBuffer_device_local.pBuffer),
                count = indices.size
            )
        }
    }

    suspend fun register() {
        ozcb.ozVulkan.framebuffer.fbs.asSequence().zip(drawCmd.asSequence()).forEach { (fb, cb) ->
            fb.actor.send(OzFramebuffer.Action.RegisterDraw(arrayOf(cb)))
        }
    }
    suspend fun unregister() {
        ozcb.ozVulkan.framebuffer.fbs.asSequence().zip(drawCmd.asSequence()).forEach { (fb, cb) ->
            fb.actor.send(OzFramebuffer.Action.UnRegisterDraw(arrayOf(cb)))
        }
    }


    fun afterSwapchainRecreated() {
        runBlocking {
            drawCmd = getCmd()
            register()
        }
    }



    fun destroy() {
        runBlocking {
            unregister()
        }

        vertexBuffer_device_local.destroy()
        indexBuffer_device_local.destroy()
    }
}