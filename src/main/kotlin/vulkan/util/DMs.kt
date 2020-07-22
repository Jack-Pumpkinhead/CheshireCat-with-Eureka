package vulkan.util

import kotlinx.coroutines.runBlocking
import vkk.entities.VkDescriptorSet
import vkk.entities.VkDescriptorSetLayout_Array
import vkk.entities.VkPipelineLayout
import vulkan.*
import vulkan.buffer.OzVMA
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts

/**
 * Created by CowardlyLion on 2020/6/15 23:06
 */
@Deprecated("ddd")
class DMs(
    val vma: OzVMA,
    val physicalDevice: OzPhysicalDevice,
    val descriptorPools: OzDescriptorPools,
    sss: SurfaceSwapchainSupport,
//    swapchain: OzSwapchain,
    pipelineLayouts: OzPipelineLayouts,
    device: OzDevice
) {

    val dms = DynamicMatrices(vma, physicalDevice)


    val dmDescriptors:List<DMDescriptors>
    init {
        val descriptorSets = runBlocking {
            val sets = descriptorPools.pool.allocate(
                VkDescriptorSetLayout_Array(sss.imageCount) {
                    pipelineLayouts.descriptorSetLayouts.layout_0_dynamic
                }
            ).await()
            List(sets.size) { sets[it] }
        }
        dmDescriptors = descriptorSets.map { DMDescriptors(dms, it, device) }

    }

    suspend fun update(index:Int) {
        dms.flush()
        dmDescriptors[index].update()
    }


}