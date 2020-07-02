package math.matrix

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex

/**
 * Created by CowardlyLion on 2020/6/29 17:08
 */
class View(val pos: Vec3, val rot: Vec3) {

    companion object {
        fun view(pos: Vec3, rot: Vec3) = Mat4()
            .rotateZassign(-rot.z)
            .rotateXassign(-rot.x)
            .rotateYassign(-rot.y)
            .translateAssign(-pos)

        fun viewVector(rot: Vec3): Vec3 {   //缺一个 inverse function.
            val res = glm.rotateX(Vec3(), Vec3(0, 0, 1), rot.x)
            return glm.rotateY(Vec3(), res, rot.y)
        }
        fun rotate(rot: Vec3,v: Vec3): Vec3 {   //camera视角
            var res = glm.rotateZ(Vec3(), v, rot.z)
            res = glm.rotateX(Vec3(), res, rot.x)   //糟糕的代码
            return glm.rotateY(Vec3(), res, rot.y)
        }

    }

    fun viewVector(): Vec3 = Companion.viewVector(rot)




    val mat get() = view(pos, rot)

//    var mat = view(pos, rot)
//    fun update() {
//        mat = view(pos, rot)
//    }


}