package vulkan.pipelines.descriptor

import game.main.Univ
import math.matrix.InArrModel

/**
 * Created by CowardlyLion on 2020/7/22 14:53
 */

suspend fun Univ.fetchModel(): InArrModel {
    return vulkan.descriptorSets.mvp.model.fetchModel()
}