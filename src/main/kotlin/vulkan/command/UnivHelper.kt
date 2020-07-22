package vulkan.command

import game.main.Univ
import math.matrix.InArrModel
import vulkan.concurrent.SyncArray2
import vulkan.pipelines.descriptor.SingleTextureSets

/**
 * Created by CowardlyLion on 2020/7/22 16:50
 */

fun Univ.bindSet(model: InArrModel, texture: SyncArray2<SingleTextureSets.ImageInfo>.InArr): BindMVPTexture {
    return BindMVPTexture(vulkan.pipelineLayouts, vulkan.descriptorSets, model, texture)
}
fun Univ.bindSet(model: InArrModel): BindMVP {
    return BindMVP(vulkan.pipelineLayouts, vulkan.descriptorSets, model)
}
