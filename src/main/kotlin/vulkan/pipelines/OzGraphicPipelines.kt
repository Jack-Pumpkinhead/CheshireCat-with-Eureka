package vulkan.pipelines

import mu.KotlinLogging
import vkk.vk10.structs.Extent2D
import vulkan.OzDevice
import vulkan.OzRenderPass
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/5/2 22:42
 */
class OzGraphicPipelines(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val shadermodule: OzShaderModule,
    val renderpass: OzRenderPass,
    extent2D: Extent2D
) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    var hellobuffer = OzGraphicPipelineHelloBuffer(ozVulkan, device, shadermodule, renderpass, extent2D)
    var hellomvp = OzGPUniform(ozVulkan, device, shadermodule, renderpass, extent2D)


    fun recreate(extent2D: Extent2D) {
        ozVulkan.cleanup(hellobuffer::destroy)
        ozVulkan.cleanup(hellomvp::destroy)
        hellobuffer = OzGraphicPipelineHelloBuffer(ozVulkan, device, shadermodule, renderpass, extent2D)
        hellomvp = OzGPUniform(ozVulkan, device, shadermodule, renderpass, extent2D)

    }




}