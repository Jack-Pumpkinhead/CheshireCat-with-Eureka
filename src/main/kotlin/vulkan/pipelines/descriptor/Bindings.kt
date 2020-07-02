package vulkan.pipelines.descriptor

import vkk.VkDescriptorType
import vkk.VkShaderStage
import vkk.vk10.structs.DescriptorSetLayoutBinding

/**
 * Created by CowardlyLion on 2020/5/30 21:21
 */


val binding0 = DescriptorSetLayoutBinding(
    binding = 0,
    descriptorType = VkDescriptorType.UNIFORM_BUFFER,
    descriptorCount = 1, //use for uniform array in glsl
    stageFlags = VkShaderStage.VERTEX_BIT.i,
    immutableSamplers = null
)
val binding1 = DescriptorSetLayoutBinding(
    binding = 1,
    descriptorType = VkDescriptorType.UNIFORM_BUFFER,
    descriptorCount = 1, //use for uniform array in glsl
    stageFlags = VkShaderStage.VERTEX_BIT.i,
    immutableSamplers = null
)

val binding2_dynamic = DescriptorSetLayoutBinding(
    binding = 2,
    descriptorType = VkDescriptorType.UNIFORM_BUFFER_DYNAMIC,
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


val mvp0sampler1 = DescriptorSetLayoutBinding(
    binding = 1,
    descriptorCount = 1,
    descriptorType = VkDescriptorType.COMBINED_IMAGE_SAMPLER,
    immutableSamplers = null,
    stageFlags = VkShaderStage.FRAGMENT_BIT.i
)