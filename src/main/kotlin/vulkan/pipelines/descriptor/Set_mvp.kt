package vulkan.pipelines.descriptor

import kotlinx.coroutines.runBlocking
import vkk.VkDescriptorType
import vkk.entities.VkDescriptorSetLayout_Array
import vkk.entities.VkDeviceSize
import vkk.vk10.structs.DescriptorBufferInfo
import vkk.vk10.structs.WriteDescriptorSet
import vkk.vk10.updateDescriptorSets
import vulkan.OzDescriptorPools
import vulkan.OzDevice
import vulkan.buffer.MatrixBuffer

/**
 * Created by CowardlyLion on 2020/5/30 21:34
 */
class Set_mvp(val device: OzDevice, val setLayouts: SetLayouts, val pools: OzDescriptorPools) {



    val set = pools.pool.allocate_im(setLayouts.layout_0)

    fun update(matrixBuffer: MatrixBuffer) {
        val descriptorBufferInfo = DescriptorBufferInfo(
            buffer = matrixBuffer.buffer.vkBuffer,
            offset = VkDeviceSize(0),
            range = VkDeviceSize(matrixBuffer.bytes)
        )

        val writeDescriptorSet = WriteDescriptorSet(
            dstSet = set,
            dstBinding = 0,
            dstArrayElement = 0,    //start index
            descriptorCount = 1,    //count
            descriptorType = VkDescriptorType.UNIFORM_BUFFER,
            bufferInfo = arrayOf(descriptorBufferInfo),
            imageInfo = arrayOf()
        )

        device.device.updateDescriptorSets(
            descriptorWrites = arrayOf(writeDescriptorSet),
            descriptorCopies = arrayOf()
        )

    }

}