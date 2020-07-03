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


    val layout_0 = create(bindings = arrayOf(bindingV0))

    val layout_0_dynamic = create(bindings = arrayOf(bindingV0_dynamic))
    val layout_mvp = create(bindings = arrayOf(
        bindingV0, bindingV1, bindingV2_dynamic
    ))

    @Deprecated("ddd")
    val mvp_samplerLayout = create(bindings = arrayOf(
        bindingV0, bindingF0sampler
    ))
    val layoutSampler = create(bindings = arrayOf(
        bindingF0sampler
    ))


    fun destroy() {
        list.forEach {
            device.device.destroyDescriptorSetLayout(it)
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}