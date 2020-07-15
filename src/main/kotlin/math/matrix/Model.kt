package math.matrix

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import vulkan.concurrent.SyncArray

/**
 * Created by CowardlyLion on 2020/6/29 19:00
 */
class Model(
    var pos: Vec3 = Vec3(),
    var rot: Vec3 = Vec3(),
    var scale: Float = 1F,
    val mats: SyncArray<Mat4>,
    val index: Int
) {


    companion object {
        fun model(pos: Vec3, rot: Vec3, scale: Float) =
            Mat4().translateAssign(pos)
                .rotateYassign(rot.y)
                .rotateXassign(rot.x)
                .rotateZassign(rot.z)
                .scaleAssign(scale)
    }

//    val mat get() = model(pos, rot, scale)


    var mat = model(pos, rot, scale)

    suspend fun update() {
        mat = model(pos, rot, scale)
        mats.withLock {
            it[index] = mat
        }
    }

    suspend fun destroy() {
        mats.recycle(index)
    }


}