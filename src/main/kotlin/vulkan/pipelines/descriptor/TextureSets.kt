package vulkan.pipelines.descriptor

import kotlinx.coroutines.runBlocking
import vkk.VkDescriptorType
import vkk.VkImageLayout
import vkk.entities.VkDescriptorSet
import vkk.entities.VkDeviceSize
import vkk.vk10.structs.DescriptorBufferInfo
import vkk.vk10.structs.DescriptorImageInfo
import vkk.vk10.structs.WriteDescriptorSet
import vkk.vk10.updateDescriptorSets
import vulkan.OzDescriptorPools
import vulkan.OzDevice
import vulkan.buffer.MatrixBuffer
import vulkan.image.OzImage
import vulkan.image.OzImages
import vulkan.image.Samplers

/**
 * Created by CowardlyLion on 2020/7/3 16:10
 */
@Deprecated("aaa")
class TextureSets(
    val device: OzDevice,
    val samplers: Samplers,
    val images: OzImages,
    val setLayouts: SetLayouts,
    val pools: OzDescriptorPools
) {


    val sets: List<VkDescriptorSet> = List(images.list.size) {
        pools.imagePool.allocate_im(setLayouts.layoutSampler)
    }

    init {
        update()
    }

    //update once
    private fun update() {
        val writes = images.list.zip(sets).map { (image, set) ->

            val descriptorImageInfo = DescriptorImageInfo(
                imageLayout = VkImageLayout.SHADER_READ_ONLY_OPTIMAL,
                sampler = image.sampler,
                imageView = image.imageView
            )
            WriteDescriptorSet(
                dstSet = set,
                dstBinding = 0,
                dstArrayElement = 0,    //start index
                descriptorCount = 1,    //count
                descriptorType = VkDescriptorType.COMBINED_IMAGE_SAMPLER,
                bufferInfo = arrayOf(),
                imageInfo = arrayOf(descriptorImageInfo)
            )
        }



        device.device.updateDescriptorSets(
            descriptorWrites = writes.toTypedArray(),
            descriptorCopies = arrayOf()
        )

    }

    fun destroy() {
        runBlocking {
            sets.forEach {
                pools.pool.free(it)
            }
        }
    }


}