package vulkan.pipelines.descriptor

import kotlinx.coroutines.runBlocking
import vkk.VkDescriptorType
import vkk.VkImageLayout
import vkk.entities.VkDescriptorSet
import vkk.entities.VkImageView
import vkk.entities.VkSampler
import vkk.vk10.structs.DescriptorImageInfo
import vkk.vk10.structs.WriteDescriptorSet
import vkk.vk10.updateDescriptorSets
import vulkan.OzDescriptorPools
import vulkan.OzDevice
import vulkan.concurrent.OzDescriptorPool
import vulkan.concurrent.SyncArray2
import vulkan.image.OzImages
import vulkan.image.Samplers

/**
 * Created by CowardlyLion on 2020/7/21 12:25
 */
class SingleTextureSets(
    val device: OzDevice,
    val setLayouts: SetLayouts,
    val pools: OzDescriptorPools
) {

    //for setLayouts.layoutSampler
    class ImageInfo(
        var imageView: VkImageView,
        var sampler: VkSampler,
        val pool: OzDescriptorPool,
        val set: VkDescriptorSet
    ) {
        val descriptorImageInfo get() = DescriptorImageInfo(
            imageLayout = VkImageLayout.SHADER_READ_ONLY_OPTIMAL,
            sampler = sampler,
            imageView = imageView
        )
        val write get() = WriteDescriptorSet(
            dstSet = set,
            dstBinding = 0,
            dstArrayElement = 0,    //start index
            descriptorCount = 1,    //count
            descriptorType = VkDescriptorType.COMBINED_IMAGE_SAMPLER,
            bufferInfo = arrayOf(),
            imageInfo = arrayOf(descriptorImageInfo)
        )
        suspend fun destroy() {
            pool.free(set)
        }
    }

    val imageInfos = SyncArray2<ImageInfo>()


    suspend fun put(imageView: VkImageView, sampler: VkSampler): SyncArray2<ImageInfo>.InArr {
        return imageInfos.assign(
            ImageInfo(
                imageView,
                sampler,
                pools.imagePool,
                pools.imagePool.allocate(setLayouts.layoutSampler).await()
            )
        )
    }


    suspend fun update() {
        imageInfos.withLockS { list ->
            val toUpdate = list.filter {
                it.changed && it.active && !it.markDestroyed
            }
            if (toUpdate.isNotEmpty()) {
                val writes = toUpdate.map {
                    it.changed = false
                    it.obj.write
                }
                device.device.updateDescriptorSets(
                    descriptorWrites = writes.toTypedArray(),
                    descriptorCopies = arrayOf()
                )
            }
        }
    }



    suspend fun clear() {
        imageInfos.withLockS { list->
            list.forEach {
                it.obj.destroy()
                it.index = -1
            }
            list.clear()
        }
    }


}