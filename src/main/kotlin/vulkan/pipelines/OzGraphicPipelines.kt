package vulkan.pipelines

import vkk.vk10.structs.Extent2D
import vulkan.OzDevice
import vulkan.OzRenderPasses
import vulkan.OzVulkan
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts

/**
 * Created by CowardlyLion on 2020/5/2 22:42
 */
class OzGraphicPipelines(
    val device: OzDevice,
    shadermodule: OzShaderModules,
    pipelineLayouts: OzPipelineLayouts,
    renderpass: OzRenderPasses,
    extent2D: Extent2D
) {

    val hellobuffer =
        OzGraphicPipelineHelloBuffer(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)
    val hellomvp = OzGPUniform(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)
    val hellomvp2 = OzGPUniformDynamic(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)
    val hellomvp3 = PipelineBasic(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)
    val hellomvp4 = PipelineBasic2(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)
//    val helloSampler = GPTextured(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)
    val hellotexture = PipelineTextured(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)
    val helloline = PipelineLine(device, shadermodule, pipelineLayouts, renderpass, 0, extent2D)


    fun destroy() {
        hellobuffer.destroy()
        hellomvp.destroy()
        hellomvp2.destroy()
        hellomvp3.destroy()
        hellomvp4.destroy()
        hellotexture.destroy()
        helloline.destroy()
//        helloSampler.destroy()
        OzVulkan.logger.info {
            "graphicPipelines destroyed"
        }
    }


}