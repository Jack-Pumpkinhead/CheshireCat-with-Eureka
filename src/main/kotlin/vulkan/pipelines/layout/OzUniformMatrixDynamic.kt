package vulkan.pipelines.layout

import kotlinx.coroutines.runBlocking
import vkk.VkDescriptorType
import vkk.entities.*
import vkk.vk10.structs.DescriptorBufferInfo
import vkk.vk10.structs.WriteDescriptorSet
import vkk.vk10.updateDescriptorSets
import vulkan.*
import vulkan.buffer.OzVMA
import vulkan.concurrent.OzMatrixBuffer
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/5/10 22:16
 */
class OzUniformMatrixDynamic(
    val device: OzDevice,
    val sss: SurfaceSwapchainSupport,
    val descriptorPools: OzDescriptorPools,
    val descriptorSetLayouts: OzDescriptorSetLayouts,
    val vma: OzVMA,
    val physicalDevice: OzPhysicalDevice
) {
    //decide if  use array of object  or  single object per image

    val descriptorSets: List<VkDescriptorSet>


    init {
        descriptorSets = runBlocking {
            val sets = descriptorPools.pool.allocate(
                VkDescriptorSetLayout_Array(sss.imageCount) { descriptorSetLayouts.layout_0_dynamic }
            ).await()
            List(sets.size) { sets[it] }
        }
    }

    //different dynamicMatrixBuffer per image
    val matrixBuffers = List(descriptorSets.size){ OzMatrixBuffer(vma, physicalDevice)}
    val bytesCache = MutableList(descriptorSets.size) { 0 }

    fun update(index: Int) {
        val mb = matrixBuffers[index]
        runBlocking {
            mb.flush()
        }
        if (bytesCache[index] >= mb.bytes) return
        bytesCache[index] = mb.bytes

        val descriptorBufferInfo = DescriptorBufferInfo(
            buffer = mb.buffer.vkBuffer,
            offset = VkDeviceSize(0),
            range = VkDeviceSize(mb.bytes)
        )

        val writeDescriptorSet = WriteDescriptorSet(
            dstSet = descriptorSets[index],
            dstBinding = 0,
            dstArrayElement = 0,    //start index
            descriptorCount = 1,    //count
            descriptorType = VkDescriptorType.UNIFORM_BUFFER_DYNAMIC,
            bufferInfo = arrayOf(descriptorBufferInfo),
            imageInfo = arrayOf()
            //where is texelBufferView?
        )

        device.device.updateDescriptorSets(
            descriptorWrites = arrayOf(writeDescriptorSet),
            descriptorCopies = arrayOf()
        )

    }

    fun destroy() {
        runBlocking {
            descriptorSets.forEach {
                descriptorPools.pool.free(VkDescriptorSet_Array(listOf(it)))
            }
        }
        matrixBuffers.forEach {
            it.destroy()
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}