package vulkan.pipelines

import game.input.GLSLoader
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
class OzShaderModules(val device: OzDevice, val glsl: GLSLoader) {

    val map = hashMapOf<String, VkShaderModule>()

    fun get(path: String): VkShaderModule = map.computeIfAbsent(path) {
        device.device.createShaderModule(
            ShaderModuleCreateInfo(code = glsl.ofGLSL(path))
        )
    }

    /**
     * @param stage fill in manually if can't resolve suffix of path
     * */
    fun getPipelineShaderStageCI(path: String, stage: VkShaderStage = VkShaderStage.ALL): PipelineShaderStageCreateInfo {
//        OzVulkan.logger.info { path }
        return PipelineShaderStageCreateInfo(
            stage = shaderStageOf(path, stage),
            module = get(path),
            name = "main"   //standard entry point
        )

    }

    /**
     * @param stage fill in manually if can't resolve suffix of path
     * */
    fun shaderStageOf(path: String, stage: VkShaderStage = VkShaderStage.ALL) =
        when (path.substringAfterLast('.', "glsl")) {
            "vert" -> VkShaderStage.VERTEX_BIT
            "frag" -> VkShaderStage.FRAGMENT_BIT
            else -> stage
        }



    fun destroy() {
        map.values.forEach {
            device.device.destroy(it)
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }



}