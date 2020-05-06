package vulkan.pipelines

import mu.KotlinLogging
import org.lwjgl.vulkan.VkWriteDescriptorSet
import vkk.VkDescriptorType
import vkk.entities.VkBuffer
import vkk.entities.VkDescriptorSetLayout_Array
import vkk.entities.VkDescriptorSet_Array
import vkk.entities.VkDeviceSize
import vkk.vk10.allocateDescriptorSets
import vkk.vk10.structs.CopyDescriptorSet
import vkk.vk10.structs.DescriptorBufferInfo
import vkk.vk10.structs.DescriptorSetAllocateInfo
import vkk.vk10.structs.WriteDescriptorSet
import vkk.vk10.updateDescriptorSets
import vulkan.OzDevice
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/5/5 16:12
 */
class OzDescriptorSets(
    ozVulkan: OzVulkan,
    val device: OzDevice,
    val descriptorPool: OzDescriptorPool,
    val ozGPUniform: OzGPUniform,
    val size: Int,
    val buffer: List<VkBuffer>,
    val range:Int
) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val sets: VkDescriptorSet_Array
    init {

        sets = device.device.allocateDescriptorSets(
            DescriptorSetAllocateInfo(
                descriptorPool = descriptorPool.descriptorPool,
                setLayouts = VkDescriptorSetLayout_Array(size) { ozGPUniform.descriptorSetLayout }
            )
        )
        for (i in 0 until size) {
            val descriptorBufferInfo = DescriptorBufferInfo(
                buffer = buffer[i],
                offset = VkDeviceSize(0),
                range = VkDeviceSize(range)
            )

            val writeDescriptorSet = WriteDescriptorSet(
                dstSet = sets[i],
                dstBinding = 0,
                dstArrayElement = 0,    //start index
                descriptorCount = 1,    //count
                descriptorType = VkDescriptorType.UNIFORM_BUFFER,
                bufferInfo = arrayOf(descriptorBufferInfo),
                imageInfo = arrayOf()
                //where is texelBufferView?
            )

            device.device.updateDescriptorSets(
                descriptorWrites = arrayOf(writeDescriptorSet),
                descriptorCopies = arrayOf()
            )

        }



    }

    fun update() {
        for (i in 0 until size) {
            val descriptorBufferInfo = DescriptorBufferInfo(
                buffer = buffer[i],
                offset = VkDeviceSize(0),
                range = VkDeviceSize(range)
            )

            val writeDescriptorSet = WriteDescriptorSet(
                dstSet = sets[i],
                dstBinding = 0,
                dstArrayElement = 0,    //start index
                descriptorCount = 1,    //count
                descriptorType = VkDescriptorType.UNIFORM_BUFFER,
                bufferInfo = arrayOf(descriptorBufferInfo),
                imageInfo = arrayOf()
                //where is texelBufferView?
            )

            device.device.updateDescriptorSets(
                descriptorWrites = arrayOf(writeDescriptorSet),
                descriptorCopies = arrayOf()
            )

        }
    }

}