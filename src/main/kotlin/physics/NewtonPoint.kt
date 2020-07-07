package physics

import glm_.vec3.Vec3

/**
 * Created by CowardlyLion on 2020/7/6 21:56
 */
class NewtonPoint(
    var p: Vec3 = Vec3(),
    var v: Vec3 = Vec3(),
    var a: Vec3 = Vec3(),
    var f: Vec3 = Vec3(),
    var m: Float = 1F,
    var dt: Float = 0.1F
) {
    fun update() {
        f.div(m, a) //a = f/m
        v.plusAssign(a.times(dt))
        p.plusAssign(v.times(dt))
    }


}