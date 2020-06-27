package vulkan.pipelines

import vkk.entities.VkPipeline
import vkk.entities.VkPipelineCache
import vkk.vk10.createGraphicsPipeline
import vkk.vk10.structs.*
import vulkan.OzDevice
import vulkan.OzRenderPasses
import vulkan.OzVulkan
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.Vertex_p3o2

/**
 * Created by CowardlyLion on 2020/6/1 18:15
 */
class GPTextured(
    val device: OzDevice,
    shadermodule: OzShaderModules,
    pipelineLayouts: OzPipelineLayouts,
    renderPasses: OzRenderPasses,
    subpass: Int = 0,
    extent2D: Extent2D
){

    val graphicsPipeline: VkPipeline

    init {


        val graphicsPipelineCI = GraphicsPipelineCreateInfo(
            stages = arrayOf(
                shadermodule.getPipelineShaderStageCI("hellosampler.vert"),
                shadermodule.getPipelineShaderStageCI("hellosampler.frag")
            ),
            vertexInputState = Vertex_p3o2.inputState,
            inputAssemblyState = inputAssemblyStateCI,
            viewportState = viewportState(extent2D),
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI,
            depthStencilState = null,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = pipelineLayouts.uniformSingle,
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
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }


}