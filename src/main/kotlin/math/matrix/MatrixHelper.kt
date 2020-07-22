package math.matrix

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3

/**
 * Created by CowardlyLion on 2020/7/20 23:13
 */

fun makeModel(pos: Vec3, rot: Vec3, scale: Vec3) =
    Mat4().translateAssign(pos)
        .rotateYassign(rot.y)
        .rotateXassign(rot.x)
        .rotateZassign(rot.z)
        .scaleAssign(scale)
fun Mat4.makeModel(pos: Vec3, rot: Vec3, scale: Vec3) =
    this.identity().translateAssign(pos)
        .rotateYassign(rot.y)
        .rotateXassign(rot.x)
        .rotateZassign(rot.z)
        .scaleAssign(scale)
