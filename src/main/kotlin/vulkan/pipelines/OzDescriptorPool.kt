package vulkan.pipelines

import mu.KotlinLogging
import vkk.VkDescriptorPoolCreate
import vkk.VkDescriptorType
import vkk.entities.VkDescriptorPool
import vkk.vk10.createDescriptorPool
import vkk.vk10.structs.DescriptorPoolCreateInfo
import vkk.vk10.structs.DescriptorPoolSize
import vulkan.OzDevice
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/5/5 16:04
 */
class OzDescriptorPool(ozVulkan: OzVulkan, val device: OzDevice, size: Int) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val descriptorPool: VkDescriptorPool

    init {
        val descriptorPoolSize = DescriptorPoolSize(
            type = VkDescriptorType.UNIFORM_BUFFER,
            descriptorCount = size
        )
        descriptorPool = device.device.createDescriptorPool(
            DescriptorPoolCreateInfo(
                flags = VkDescriptorPoolCreate(0).i,
                poolSizes = arrayOf(descriptorPoolSize),
                maxSets = size
            )
        )

    }

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        device.device.destroy(descriptorPool)
    }

}