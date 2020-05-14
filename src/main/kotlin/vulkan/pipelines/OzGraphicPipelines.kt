package vulkan.pipelines

import vkk.vk10.structs.Extent2D
import vulkan.OzDevice
import vulkan.OzRenderPass
import vulkan.OzVulkan
import vulkan.pipelines.layout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.OzVertexInputs

/**
 * Created by CowardlyLion on 2020/5/2 22:42
 */
class OzGraphicPipelines(
    val device: OzDevice,
    shadermodule: OzShaderModules,
    vertexInputs: OzVertexInputs,
    pipelineLayouts: OzPipelineLayouts,
    renderpass: OzRenderPass,
    extent2D: Extent2D
) {

    var hellobuffer =
        OzGraphicPipelineHelloBuffer(device, shadermodule, vertexInputs, pipelineLayouts, renderpass, 0, extent2D)
    var hellomvp = OzGPUniform(device, shadermodule, vertexInputs, pipelineLayouts, renderpass, 0, extent2D)
    var hellomvp2 = OzGPUniformDynamic(device, shadermodule, vertexInputs, pipelineLayouts, renderpass, 0, extent2D)


    fun destroy() {
        hellobuffer.destroy()
        hellomvp.destroy()
        hellomvp2.destroy()
        OzVulkan.logger.info {
            "graphicPipelines destroyed"
        }
    }




}