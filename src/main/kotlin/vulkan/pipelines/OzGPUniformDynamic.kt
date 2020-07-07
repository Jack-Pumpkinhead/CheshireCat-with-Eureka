package vulkan.pipelines

import vkk.entities.VkPipeline
import vkk.entities.VkPipelineCache
import vkk.vk10.createGraphicsPipeline
import vkk.vk10.structs.*
import vulkan.OzDevice
import vulkan.OzRenderPasses
import vulkan.OzVulkan
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.VertexInput

/**
 * Created by CowardlyLion on 2020/5/10 21:43
 */
class OzGPUniformDynamic(
    val device: OzDevice,
    shadermodule: OzShaderModules,
    pipelineLayouts: OzPipelineLayouts,
    renderPasses: OzRenderPasses,
    subpass: Int = 0,
    extent2D: Extent2D
){

    val layout = pipelineLayouts.uniformDynamic
    val graphicsPipeline: VkPipeline

    init {

        val graphicsPipelineCI = GraphicsPipelineCreateInfo(
            stages = arrayOf(shadermodule.getPipelineShaderStageCI("hellomvp2.vert"),
                shadermodule.getPipelineShaderStageCI("basic.frag")
            ),
            vertexInputState = VertexInput.P3C3,
            inputAssemblyState = TriangleList,
            viewportState = viewportState(extent2D),
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI,
            depthStencilState = null,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = layout,
            renderPass = renderPasses.renderpass,
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
        OzVulkan.logger.debug {
            "pipeline 'hellomvp2' destroyed"
        }
    }


}