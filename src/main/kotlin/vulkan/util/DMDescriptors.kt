package vulkan.util

import kotlinx.coroutines.runBlocking
import vkk.VkDescriptorType
import vkk.entities.VkDescriptorSet
import vkk.entities.VkDescriptorSet_Array
import vkk.entities.VkDeviceSize
import vkk.vk10.structs.DescriptorBufferInfo
import vkk.vk10.structs.WriteDescriptorSet
import vkk.vk10.updateDescriptorSets
import vulkan.OzDevice
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/6/15 22:51
 */
@Deprecated("ddd")
class DMDescriptors(
    val matrices: DynamicMatrices,
    val descriptorSets: VkDescriptorSet,
    val device: OzDevice
) {
    suspend fun update() {
        matrices.matrices.withLock {


            val descriptorBufferInfo = DescriptorBufferInfo(
                buffer = matrices.buffer.vkBuffer,
                offset = VkDeviceSize(0),
                range = VkDeviceSize(matrices.dynamicAlignment.toInt() * it.size)
            )

            val writeDescriptorSet = WriteDescriptorSet(
                dstSet = descriptorSets,
                dstBinding = 0,
                dstArrayElement = 0,    //start index
                descriptorCount = 1,    //count
                descriptorType = VkDescriptorType.UNIFORM_BUFFER_DYNAMIC,
                bufferInfo = arrayOf(descriptorBufferInfo),
                imageInfo = arrayOf()
            )

            device.device.updateDescriptorSets(
                descriptorWrites = arrayOf(writeDescriptorSet),
                descriptorCopies = arrayOf()
            )

        }
    }

}