package vulkan.pipelines

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vkk.VkPipelineBindPoint
import vkk.entities.VkDescriptorSet_Array
import vkk.entities.VkPipeline
import vkk.entities.VkPipelineCache
import vkk.entities.VkPipelineLayout
import vkk.identifiers.CommandBuffer
import vkk.vk10.bindDescriptorSets
import vkk.vk10.createGraphicsPipeline
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.GraphicsPipelineCreateInfo
import vulkan.OzDevice
import vulkan.OzRenderPasses
import vulkan.OzVulkan
import vulkan.drawing.OzObjectSimple
import vulkan.drawing.OzObjectTextured2
import vulkan.drawing.StaticObject
import vulkan.pipelines.descriptor.LayoutMVP
import vulkan.pipelines.descriptor.TextureSets
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.VertexInput

/**
 * Created by CowardlyLion on 2020/7/16 20:06
 */
class PipelineVertexOnly(
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
                shadermodule.getPipelineShaderStageCI("vertexOnly.vert"),
                shadermodule.getPipelineShaderStageCI("vertexOnly.frag")
            ),
            vertexInputState = VertexInput.P3,
            inputAssemblyState = TriangleList,
            viewportState = viewportState(extent2D),
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI,
            depthStencilState = depthStencilState,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = layout,
            renderPass = renderPasses.renderpass_depth,
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

    class MultiObject(
        val vulkan: OzVulkan,
        val data: StaticObject
    ){
        inline val pipeline get() = vulkan.graphicPipelines.vertexOnly.graphicsPipeline
        inline val pipelineLayout get() = vulkan.graphicPipelines.vertexOnly.layout
        inline val layoutMVP get() = vulkan.layoutMVP



        val objs = mutableListOf<OzObjectSimple>()

        val mutex = Mutex()

        suspend fun record(cb: CommandBuffer, imageIndex: Int) {
            cb.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline
            )
//            cb.nextSubpass()
            data.bind(cb)
            mutex.withLock {
                objs.forEach {obj->
                    cb.bindDescriptorSets(
                        pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                        layout = pipelineLayout,
                        firstSet = 0,
                        descriptorSets = VkDescriptorSet_Array(
                            listOf(layoutMVP.sets[imageIndex])
                        ),
                        dynamicOffsets = intArrayOf(obj.model.index * layoutMVP.model.dynamicAlignment.toInt())
                    )
                    data.draw(cb)
                }
            }
        }

    }

}