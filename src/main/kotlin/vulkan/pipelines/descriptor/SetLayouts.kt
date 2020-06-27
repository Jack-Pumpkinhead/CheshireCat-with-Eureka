package vulkan.pipelines.descriptor

import vkk.VkDescriptorSetLayoutCreate
import vkk.entities.VkDescriptorSetLayout
import vkk.vk10.createDescriptorSetLayout
import vkk.vk10.structs.DescriptorSetLayoutBinding
import vkk.vk10.structs.DescriptorSetLayoutCreateInfo
import vulkan.OzDevice
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/5/9 23:10
 */
class SetLayouts(val device: OzDevice) {

    val list = mutableListOf<VkDescriptorSetLayout>()

    fun create(
        flags: VkDescriptorSetLayoutCreate = VkDescriptorSetLayoutCreate(0),
        bindings: Array<DescriptorSetLayoutBinding>? = null
    ): VkDescriptorSetLayout {
        val setLayout = device.device.createDescriptorSetLayout(
            DescriptorSetLayoutCreateInfo(flags = flags.i, bindings = bindings)
        )
        list += setLayout
        return setLayout
    }


    val layout_0 = create(bindings = arrayOf(binding0))

    val layout_0_dynamic = create(bindings = arrayOf(binding0_dynamic))

    val mvp_samplerLayout = create(bindings = arrayOf(binding0, mvp0sampler1))


    fun destroy() {
        list.forEach {
            device.device.destroyDescriptorSetLayout(it)
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}