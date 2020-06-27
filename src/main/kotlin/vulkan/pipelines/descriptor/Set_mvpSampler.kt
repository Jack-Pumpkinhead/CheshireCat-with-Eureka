package vulkan.pipelines.descriptor

import vkk.VkDescriptorType
import vkk.VkImageLayout
import vkk.entities.VkDeviceSize
import vkk.vk10.structs.DescriptorBufferInfo
import vkk.vk10.structs.DescriptorImageInfo
import vkk.vk10.structs.WriteDescriptorSet
import vkk.vk10.updateDescriptorSets
import vulkan.OzDescriptorPools
import vulkan.OzDevice
import vulkan.buffer.MatrixBuffer
import vulkan.image.OzImage
import vulkan.image.Samplers

/**
 * Created by CowardlyLion on 2020/5/30 22:45
 */
class Set_mvpSampler(
    val device: OzDevice,
    val setLayouts: SetLayouts,
    val pools: OzDescriptorPools,
    val samplers: Samplers
) {



    val set = pools.pool.allocate_im(setLayouts.mvp_samplerLayout)

    fun update(mvp: MatrixBuffer, image: OzImage) {
        val descriptorBufferInfo = DescriptorBufferInfo(
            buffer = mvp.buffer.vkBuffer,
            offset = VkDeviceSize(0),
            range = VkDeviceSize(mvp.bytes)
        )
        val descriptorImageInfo = DescriptorImageInfo(
            imageLayout = VkImageLayout.SHADER_READ_ONLY_OPTIMAL,
            sampler = samplers.sampler,
            imageView = image.imageView
        )

        val write1 = WriteDescriptorSet(
            dstSet = set,
            dstBinding = 0,
            dstArrayElement = 0,    //start index
            descriptorCount = 1,    //count
            descriptorType = VkDescriptorType.UNIFORM_BUFFER,
            bufferInfo = arrayOf(descriptorBufferInfo),
            imageInfo = arrayOf()
        )
        val write2 = WriteDescriptorSet(
            dstSet = set,
            dstBinding = 1,
            dstArrayElement = 0,    //start index
            descriptorCount = 1,    //count
            descriptorType = VkDescriptorType.COMBINED_IMAGE_SAMPLER,
            bufferInfo = arrayOf(),
            imageInfo = arrayOf(descriptorImageInfo)
        )


        device.device.updateDescriptorSets(
            descriptorWrites = arrayOf(write1,write2),
            descriptorCopies = arrayOf()
        )

    }

}