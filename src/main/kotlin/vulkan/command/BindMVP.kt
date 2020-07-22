package vulkan.command

import math.matrix.InArrModel
import vkk.VkPipelineBindPoint
import vkk.entities.VkDescriptorSet_Array
import vkk.identifiers.CommandBuffer
import vkk.vk10.bindDescriptorSets
import vulkan.pipelines.descriptor.OzDescriptorSets
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts

/**
 * Created by CowardlyLion on 2020/7/22 16:47
 */
class BindMVP(
    val pipelineLayouts: OzPipelineLayouts,
    val descriptorSets: OzDescriptorSets,
    val model: InArrModel
) : BindDescriptorSets {

    override fun bind(cb: CommandBuffer, imageIndex: Int) {
        cb.bindDescriptorSets(
            pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
            layout = pipelineLayouts.mvp,
            firstSet = 0,
            descriptorSets = VkDescriptorSet_Array(
                listOf(descriptorSets.mvp.sets[imageIndex])
            ),
            dynamicOffsets = intArrayOf(model.mat.index * descriptorSets.mvp.model.dynamicAlignment)
        )
    }
}