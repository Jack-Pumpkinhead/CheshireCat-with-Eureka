package vulkan.pipelines.descriptor

import kotlinx.coroutines.runBlocking
import vkk.VkDescriptorType
import vkk.entities.VkDescriptorSet
import vkk.entities.VkDescriptorSetLayout_Array
import vkk.entities.VkDeviceSize
import vkk.vk10.structs.DescriptorBufferInfo
import vkk.vk10.structs.WriteDescriptorSet
import vkk.vk10.updateDescriptorSets
import vulkan.OzDescriptorPools
import vulkan.OzDevice
import vulkan.OzPhysicalDevice
import vulkan.OzVulkan
import vulkan.buffer.MatrixBuffer
import vulkan.buffer.MatrixBufferC
import vulkan.buffer.OzVMA
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.util.DMDescriptors
import vulkan.util.DynamicMatrices
import vulkan.util.DynamicModel
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/6/28 22:50
 */
class LayoutMVP(
    val vma: OzVMA,
    physicalDevice: OzPhysicalDevice,
    val descriptorPools: OzDescriptorPools,
    sss: SurfaceSwapchainSupport,
    pipelineLayouts: OzPipelineLayouts,
    val device: OzDevice
) {
    val proj = MatrixBufferC(vma)
    val view = MatrixBufferC(vma)
    val model = DynamicModel(vma,physicalDevice)

    val sets:List<VkDescriptorSet>
    init {
        sets = runBlocking {
            val sets = descriptorPools.pool.allocate(
                VkDescriptorSetLayout_Array(sss.imageCount) {
                    pipelineLayouts.descriptorSetLayouts.layout_mvp
                }
            ).await()
            List(sets.size) { sets[it] }
        }
    }

    suspend fun update(index:Int) {
        proj.flush()
        view.flush()
        val matSize = model.flush()
        update(sets[index],matSize)
    }


    fun update(set: VkDescriptorSet, matSize: Int) {


        val writeProj = WriteDescriptorSet(
            dstSet = set,
            dstBinding = 0,
            dstArrayElement = 0,    //start index
            descriptorCount = 1,    //count
            descriptorType = VkDescriptorType.UNIFORM_BUFFER,
            bufferInfo = arrayOf(proj.descriptorBI()),
            imageInfo = arrayOf()
        )
        val writeView = WriteDescriptorSet(
            dstSet = set,
            dstBinding = 1,
            dstArrayElement = 0,    //start index
            descriptorCount = 1,    //count
            descriptorType = VkDescriptorType.UNIFORM_BUFFER,
            bufferInfo = arrayOf(view.descriptorBI()),
            imageInfo = arrayOf()
        )

        val modelBI = DescriptorBufferInfo(
            buffer = model.buffer.vkBuffer,
            offset = VkDeviceSize(0),
            range = VkDeviceSize(model.dynamicAlignment.toInt() * matSize)
        )
        val writeModel = WriteDescriptorSet(
            dstSet = set,
            dstBinding = 2,
            dstArrayElement = 0,    //start index
            descriptorCount = 1,    //count
            descriptorType = VkDescriptorType.UNIFORM_BUFFER_DYNAMIC,
            bufferInfo = arrayOf(modelBI),
            imageInfo = arrayOf()
        )


        device.device.updateDescriptorSets(
            descriptorWrites = arrayOf(writeProj,writeView,writeModel),
            descriptorCopies = arrayOf()
        )

    }

    fun destroy() {
        proj.destroy()
        view.destroy()
        model.destroy()
        runBlocking {
            sets.forEach {
                descriptorPools.pool.free(it)
            }
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}