package vulkan.pipelines

import game.main.Recorder3
import kool.BYTES
import kool.Stack
import kotlinx.coroutines.runBlocking
import vkk.VkIndexType
import vkk.VkPipelineBindPoint
import vkk.entities.*
import vkk.vk10.bindDescriptorSets
import vkk.vk10.bindVertexBuffers
import vkk.vk10.createGraphicsPipeline
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.GraphicsPipelineCreateInfo
import vulkan.OzDevice
import vulkan.OzRenderPasses
import vulkan.OzVulkan
import vulkan.buffer.OzVMA
import vulkan.buffer.VmaBuffer
import vulkan.command.CopyBuffer
import vulkan.pipelines.descriptor.LayoutMVP
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.VertexInput

/**
 * Created by CowardlyLion on 2020/7/6 18:17
 */
class PipelineLine (
    val device: OzDevice,
    shadermodule: OzShaderModules,
    pipelineLayouts: OzPipelineLayouts,
    renderPasses: OzRenderPasses,
    subpass: Int = 0,
    extent2D: Extent2D
) {
    val graphicsPipeline: VkPipeline
    val layout = pipelineLayouts.mvp

    init {
        val graphicsPipelineCI = GraphicsPipelineCreateInfo(
            stages = arrayOf(
                shadermodule.getPipelineShaderStageCI("hellomvp4.vert"),
                shadermodule.getPipelineShaderStageCI("basic.frag")
            ),
            vertexInputState = VertexInput.P3C3,
            inputAssemblyState = LineList,
            viewportState = viewportState(extent2D),
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI_MSAA,
            depthStencilState = depthStencilState,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = layout,
            renderPass = renderPasses.renderpass_depth_MSAA,
//            renderPass = renderPasses.renderpass,
            subpass = subpass,
            basePipelineHandle = VkPipeline.NULL,
            basePipelineIndex = -1
        )

        graphicsPipeline = device.device.createGraphicsPipeline(
            pipelineCache = VkPipelineCache.NULL,
            createInfo = graphicsPipelineCI
        )
    }
    fun destroy() {
        device.device.destroy(graphicsPipeline)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

    class ObjStatic(
//        val vulkan: OzVulkan,
        val vma: OzVMA,
        val copyBuffer: CopyBuffer,
        val pipeline:PipelineBasic2,
        val layoutMVP: LayoutMVP,
        val vert_color: FloatArray,
        val indices: IntArray,
        var matrixIndex: Int
    ) {
        val vbytes = vert_color.size * Float.BYTES
        val ibytes = indices.size * Int.BYTES


        val vertexBuffer_device_local: VmaBuffer
        val indexBuffer_device_local: VmaBuffer

        init {

            val vertexBuffer = vma.createBuffer_vertexStaging(vbytes)
            val indexBuffer = vma.createBuffer_indexStaging(ibytes)

            Stack {
                vertexBuffer.memory.fill(
                    it.mallocFloat(vert_color.size).put(vert_color).flip()
                )
                indexBuffer.memory.fill(
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

        }






        val recorder: Recorder3 = { cb,imageIndex ->
            cb.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline.graphicsPipeline
            )
            cb.bindVertexBuffers(
                firstBinding = 0,
                bindingCount = 1,
                buffers = VkBuffer_Array(listOf(vertexBuffer_device_local.vkBuffer)),
                offsets = VkDeviceSize_Array(listOf(VkDeviceSize(0)))
            )

            cb.bindIndexBuffer(
                buffer = indexBuffer_device_local.vkBuffer,
                offset = VkDeviceSize(0),
                indexType = VkIndexType.UINT32
            )

            cb.bindDescriptorSets(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                layout = pipeline.layout,
                firstSet = 0,
                descriptorSets = VkDescriptorSet_Array(
                    listOf(layoutMVP.sets[imageIndex])
                ),
                dynamicOffsets = intArrayOf(matrixIndex * layoutMVP.model.dynamicAlignment.toInt())
            )

            cb.drawIndexed(
                indexCount = indices.size,
                instanceCount = 1,
                firstIndex = 0,
                vertexOffset = 0,
                firstInstance = 0
            )
        }

    }
}