package vulkan.pipelines.pipelineLayout

import vkk.entities.VkDescriptorSetLayout_Array
import vkk.vk10.createPipelineLayout
import vkk.vk10.structs.PipelineLayoutCreateInfo
import vulkan.OzDevice
import vulkan.OzVulkan
import vulkan.pipelines.descriptor.SetLayouts

/**
 * Created by CowardlyLion on 2020/5/9 23:18
 */
class OzPipelineLayouts(val device: OzDevice, val descriptorSetLayouts: SetLayouts) {

    val empty = device.device.createPipelineLayout(
        createInfo = PipelineLayoutCreateInfo(
            setLayouts = null,
            pushConstantRanges = null
        )
    )

    val uniformSingle = device.device.createPipelineLayout(
        createInfo = PipelineLayoutCreateInfo(
            setLayouts = VkDescriptorSetLayout_Array(1) { descriptorSetLayouts.layout_0 },
            pushConstantRanges = null
        )
    )
    val uniformDynamic = device.device.createPipelineLayout(
        createInfo = PipelineLayoutCreateInfo(
            setLayouts = VkDescriptorSetLayout_Array(1) { descriptorSetLayouts.layout_0_dynamic },
            pushConstantRanges = null
        )
    )


    val pls = listOf(
        empty, uniformSingle,uniformDynamic
    )

    fun destroy() {
        pls.forEach {
            device.device.destroy(it)
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }



}