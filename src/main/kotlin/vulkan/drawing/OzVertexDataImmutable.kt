package vulkan.drawing

import kool.*
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.getBean
import vkk.entities.*
import vkk.identifiers.CommandBuffer
import vulkan.OzCommandPools
import vulkan.buffer.OzVMA
import vulkan.OzFramebuffers
import vulkan.OzSwapchain
import vulkan.OzVulkan
import vulkan.command.CopyBuffer
import vulkan.command.DrawCmd
import vulkan.pipelines.OzGraphicPipelines
import vulkan.pipelines.layout.OzUniformMatrixDynamic
import vulkan.pipelines.layout.OzPipelineLayouts

class OzVertexDataImmutable(
    vma: OzVMA,
    val copyBuffer: CopyBuffer,
    val commandPools: OzCommandPools,
    val uniformMatrixDynamic: OzUniformMatrixDynamic,

    var swapchain: OzSwapchain,
    var framebuffers: OzFramebuffers,
    var pipelines: OzGraphicPipelines,

    val vertices: FloatArray,
    val indices: IntArray,
    val objectIndex: Int,
    val ozVulkan: OzVulkan
) {

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
            copyBuffer.copyBuffer(vertexBuffer.pBuffer, vertexBuffer_device_local.pBuffer, vbytes)
            copyBuffer.copyBuffer(indexBuffer.pBuffer, indexBuffer_device_local.pBuffer, ibytes)
        }
        vertexBuffer.destroy()
        indexBuffer.destroy()

        drawCmd = runBlocking {
            getCmd()
        }
        runBlocking {
            register()
        }
    }

    suspend fun getCmd(): List<CommandBuffer> {
        //need to reload framebuffers!

        return framebuffers.fbs.mapIndexed { index, framebuffer ->
            val cb = commandPools.graphicCP.allocate().await()

            /*it.recordDraw(
                cb,
                ozcb.ozVulkan.graphicPipelines.hellobuffer.graphicsPipeline,
                buffers = VkBuffer_Array(1) { VkBuffer(vertexBuffer_device_local.pBuffer) },
                indexBuffer = VkBuffer(indexBuffer_device_local.pBuffer),
                count = indices.size
            )*/
            DrawCmd.recordDrawUniformDynamic(
                cb = cb,
                framebuffer = framebuffer,
                pipeline = pipelines.hellomvp2.graphicsPipeline,
                pipelineLayout = pipelines.hellomvp2.layout,
                descriptorSets = VkDescriptorSet_Array(
                    listOf(uniformMatrixDynamic.descriptorSets[index])
                ),
                dynamicOffsets = intArrayOf(objectIndex * uniformMatrixDynamic.matrixBuffers[index].alignment),
                buffer_array = VkBuffer_Array(listOf(VkBuffer(vertexBuffer_device_local.pBuffer))),
                offset_array = VkDeviceSize_Array(listOf(VkDeviceSize(0))),
                indexBuffer = VkBuffer(indexBuffer_device_local.pBuffer),
                count = indices.size
            )
            cb
        }
    }

    suspend fun register() {
        swapchain.images.forEachIndexed { index, image ->
            image.add(drawCmd[index])
        }
    }
    suspend fun unregister() {
        swapchain.images.forEachIndexed { index, image ->
            image.remove(drawCmd[index])
        }
    }

//    init {
//        ozVulkan.afterSwapchainRecreateEvent += this::afterSwapchainRecreated
//    }

    //注意OzObjects register/unregister影响调用
    //need to be called after swapchain recreated
    suspend fun afterSwapchainRecreated() {
        drawCmd.forEach {
            commandPools.graphicCP.free(it)
        }
        swapchain = ozVulkan.swapchainContext.getBean()
        framebuffers = ozVulkan.swapchainContext.getBean()
        pipelines = ozVulkan.swapchainContext.getBean()
        drawCmd = getCmd()
        register()
    }


    fun destroy() {
        runBlocking {
            unregister()
            drawCmd.forEach {
                commandPools.graphicCP.free(it)
            }
        }

        vertexBuffer_device_local.destroy()
        indexBuffer_device_local.destroy()
    }
}