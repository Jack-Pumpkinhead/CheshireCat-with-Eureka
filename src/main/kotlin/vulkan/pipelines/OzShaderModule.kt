package vulkan.pipelines

import graphics.scenery.spirvcrossj.EShLanguage
import mu.KotlinLogging
import vkk.VkShaderStage
import vkk.entities.VkShaderModule
import vkk.vk10.createShaderModule
import vkk.vk10.structs.PipelineShaderStageCreateInfo
import vkk.vk10.structs.ShaderModuleCreateInfo
import vulkan.OzDevice
import vulkan.OzVulkan
import vulkan.util.LoaderGLSL

/**
 * Created by CowardlyLion on 2020/5/2 20:42
 */
class OzShaderModule(ozVulkan: OzVulkan, val device: OzDevice) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val map = hashMapOf<String, VkShaderModule>()

    fun get(path: String): VkShaderModule = map.computeIfAbsent(path) {
        device.device.createShaderModule(
            ShaderModuleCreateInfo(code = LoaderGLSL.ofGLSL(path).buffer)
        )
    }

    fun getPipelineShaderStageCI(path: String, stage: VkShaderStage = VkShaderStage.ALL): PipelineShaderStageCreateInfo {
        return PipelineShaderStageCreateInfo(
            stage = vkShaderStageOf(path, stage),
            module = get(path),
            name = "main"   //standard entry point
        )

    }

    /**
     * @param stage fill in manually if don't follow default file naming convention
     * */
    fun vkShaderStageOf(path: String, stage: VkShaderStage = VkShaderStage.ALL) =
        when (path.substringAfterLast('.', "glsl")) {
            "vert" -> VkShaderStage.VERTEX_BIT
            "frag" -> VkShaderStage.FRAGMENT_BIT
            else -> stage
        }


    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        map.forEach { _, shadermodule ->
            device.device.destroy(shadermodule)
        }
    }



}