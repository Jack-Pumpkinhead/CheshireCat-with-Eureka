package vulkan.pipelines.layout

import vkk.VkDescriptorSetLayoutCreate
import vkk.VkDescriptorType
import vkk.VkShaderStage
import vkk.vk10.createDescriptorSetLayout
import vkk.vk10.structs.DescriptorSetLayoutBinding
import vkk.vk10.structs.DescriptorSetLayoutCreateInfo
import vulkan.OzDevice
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/5/9 23:10
 */
class OzDescriptorSetLayouts(val device: OzDevice) {

    val binding0 = DescriptorSetLayoutBinding(
        binding = 0,
        descriptorType = VkDescriptorType.UNIFORM_BUFFER,
        descriptorCount = 1, //use for uniform array in glsl
        stageFlags = VkShaderStage.VERTEX_BIT.i,
        immutableSamplers = null
    )
    val binding0_dynamic = DescriptorSetLayoutBinding(
        binding = 0,
        descriptorType = VkDescriptorType.UNIFORM_BUFFER_DYNAMIC,
        descriptorCount = 1, //use for uniform array in glsl
        stageFlags = VkShaderStage.VERTEX_BIT.i,
        immutableSamplers = null
    )

    val layout_0 = device.device.createDescriptorSetLayout(
        DescriptorSetLayoutCreateInfo(
            flags = VkDescriptorSetLayoutCreate(0).i,
            bindings = arrayOf(binding0)
        )
    )
    val layout_0_dynamic = device.device.createDescriptorSetLayout(
        DescriptorSetLayoutCreateInfo(
            flags = VkDescriptorSetLayoutCreate(0).i,
            bindings = arrayOf(binding0_dynamic)
        )
    )



    val ls = listOf(
        layout_0, layout_0_dynamic
    )

    fun destroy() {
        ls.forEach {
            device.device.destroyDescriptorSetLayout(it)
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}