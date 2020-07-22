package math.matrix

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import vulkan.concurrent.SyncArray2

/**
 * Created by CowardlyLion on 2020/7/20 23:11
 */
class InArrModel(
    var pos: Vec3 = Vec3(),
    var rot: Vec3 = Vec3(),
    var scale: Vec3 = Vec3(1, 1, 1),
    val mat: SyncArray2<Mat4>.InArr
) {
    suspend fun update() {
        mat.withLock {
            it.makeModel(pos, rot, scale)
        }
    }

    fun destroy() {
        mat.markDestroyed()
    }

}