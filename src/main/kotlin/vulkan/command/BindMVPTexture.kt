package vulkan.command

import math.matrix.InArrModel
import vkk.VkPipelineBindPoint
import vkk.entities.VkDescriptorSet_Array
import vkk.identifiers.CommandBuffer
import vkk.vk10.bindDescriptorSets
import vulkan.concurrent.SyncArray2
import vulkan.pipelines.descriptor.OzDescriptorSets
import vulkan.pipelines.descriptor.SingleTextureSets
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts

/**
 * Created by CowardlyLion on 2020/7/22 13:44
 */
class BindMVPTexture(
    val pipelineLayouts: OzPipelineLayouts,
    val descriptorSets: OzDescriptorSets,
    val model: InArrModel,
    var texture: SyncArray2<SingleTextureSets.ImageInfo>.InArr
) : BindDescriptorSets {

    override fun bind(cb: CommandBuffer, imageIndex: Int) {
        cb.bindDescriptorSets(
            pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
            layout = pipelineLayouts.mvp_sampler,
            firstSet = 0,
            descriptorSets = VkDescriptorSet_Array(
                listOf(descriptorSets.mvp.sets[imageIndex], texture.obj.set)
            ),
            dynamicOffsets = intArrayOf(model.mat.index * descriptorSets.mvp.model.dynamicAlignment)
        )
    }

    //destroy model即可

}