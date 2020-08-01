package vulkan.pipelines

import game.main.OzConfigurations
import kotlinx.coroutines.runBlocking
import vkk.VkSampleCount
import vkk.entities.VkPipeline
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.GraphicsPipelineCreateInfo
import vulkan.OzDevice
import vulkan.OzRenderPasses
import vulkan.OzVulkan
import vulkan.concurrent.SyncArray2
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.VertexInput

/**
 * Created by CowardlyLion on 2020/5/2 22:42
 */
class OzGraphicPipelines(
    val device: OzDevice,
    shadermodule: OzShaderModules,
    pipelineLayouts: OzPipelineLayouts,
    renderPasses: OzRenderPasses,
    extent2D: Extent2D
) {

    val hellobuffer = OzGraphicPipelineHelloBuffer(device, shadermodule, pipelineLayouts, renderPasses, 0, extent2D)
    val hellomvp = OzGPUniform(device, shadermodule, pipelineLayouts, renderPasses, 0, extent2D)
    val hellomvp2 = OzGPUniformDynamic(device, shadermodule, pipelineLayouts, renderPasses, 0, extent2D)
    val hellomvp3 = PipelineBasic(device, shadermodule, pipelineLayouts, renderPasses, 0, extent2D)
    val hellomvp4 = PipelineBasic2(device, shadermodule, pipelineLayouts, renderPasses, 0, extent2D)
//    val helloSampler = GPTextured(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)
    val hellotexture = PipelineTextured(device, shadermodule, pipelineLayouts, renderPasses, 0, extent2D)
    val helloline = PipelineLine(device, shadermodule, pipelineLayouts, renderPasses, 0, extent2D)
    val vertexOnly = PipelineVertexOnly(device, shadermodule, pipelineLayouts, renderPasses, 0, extent2D)
    //以上全 deprecated


    val pipelines = SyncArray2<GraphicPipeline>()
    suspend fun put(createInfo: GraphicsPipelineCreateInfo): SyncArray2<GraphicPipeline>.InArr {
        return pipelines.assign(GraphicPipeline(device, createInfo))
    }
    fun put_im(createInfo: GraphicsPipelineCreateInfo): SyncArray2<GraphicPipeline>.InArr {
        return runBlocking {
            pipelines.assign(GraphicPipeline(device, createInfo))
        }
    }


    val singleTexture = put_im(
            GraphicsPipelineCreateInfo(
                stages = arrayOf(
                    shadermodule.getPipelineShaderStageCI("hellosampler2.vert"),
                    shadermodule.getPipelineShaderStageCI("hellosampler2.frag")
                ),
                vertexInputState = VertexInput.P3T2,
                inputAssemblyState = TriangleList,
                viewportState = viewportState(extent2D),
                rasterizationState = rasterizationSCI,
                multisampleState = multisampleSCI_MSAA,
                depthStencilState = depthStencilState,
                colorBlendState = colorBlendSCI,
                dynamicState = null,
                layout = pipelineLayouts.mvp_sampler,
                renderPass = renderPasses.renderpass_depth_MSAA,
                subpass = 0,
                basePipelineHandle = VkPipeline.NULL,
                basePipelineIndex = -1
            )
        )
    val line = put_im(
        GraphicsPipelineCreateInfo(
            stages = arrayOf(
                shadermodule.getPipelineShaderStageCI("hellomvp4.vert"),
                shadermodule.getPipelineShaderStageCI("basic.frag")
            ),
            vertexInputState = VertexInput.P3C3,
            inputAssemblyState = LineList,
            viewportState = viewportState(extent2D),
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI(OzConfigurations.MSAA, 1.0F),
            depthStencilState = depthStencilState,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = pipelineLayouts.mvp,
            renderPass = renderPasses.renderpass_depth_MSAA,
//            renderPass = renderPasses.renderpass,
            subpass = 0,
            basePipelineHandle = VkPipeline.NULL,
            basePipelineIndex = -1
        )
    )
    val line_thin = put_im(
        GraphicsPipelineCreateInfo(
            stages = arrayOf(
                shadermodule.getPipelineShaderStageCI("hellomvp4.vert"),
                shadermodule.getPipelineShaderStageCI("basic.frag")
            ),
            vertexInputState = VertexInput.P3C3,
            inputAssemblyState = LineList,
            viewportState = viewportState(extent2D),
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI(VkSampleCount._4_BIT, 1.0F),
            depthStencilState = depthStencilState,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = pipelineLayouts.mvp,
            renderPass = renderPasses.renderpass_depth_MSAA,
//            renderPass = renderPasses.renderpass,
            subpass = 0,
            basePipelineHandle = VkPipeline.NULL,
            basePipelineIndex = -1
        )
    )


    val triangle = put_im(
        GraphicsPipelineCreateInfo(
            stages = arrayOf(
                shadermodule.getPipelineShaderStageCI("hellomvp4.vert"),
                shadermodule.getPipelineShaderStageCI("basic.frag")
            ),
            vertexInputState = VertexInput.P3C3,
            inputAssemblyState = TriangleList,
            viewportState = viewportState(extent2D),
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI_MSAA,
            depthStencilState = depthStencilState,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout =  pipelineLayouts.mvp,
            renderPass = renderPasses.renderpass_depth_MSAA,
//            renderPass = renderPasses.renderpass,
            subpass = 0,
            basePipelineHandle = VkPipeline.NULL,
            basePipelineIndex = -1
        )
    )


    fun destroy() {
        hellobuffer.destroy()
        hellomvp.destroy()
        hellomvp2.destroy()
        hellomvp3.destroy()
        hellomvp4.destroy()
        hellotexture.destroy()
        helloline.destroy()

        runBlocking {
            pipelines.withLockS {list->
                list.forEach {
                    it.index = -1
                    device.device.destroy(it.obj.graphicsPipeline)
                }
                list.clear()
            }
        }
//        helloSampler.destroy()
        OzVulkan.logger.info {
            "graphicPipelines destroyed"
        }
    }


}