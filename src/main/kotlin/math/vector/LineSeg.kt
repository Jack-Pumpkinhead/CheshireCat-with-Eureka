package math.vector

import glm_.vec3.Vec3

/**
 * Created by CowardlyLion on 2020/7/31 12:42
 */
class LineSeg(val a: Vec3, val b: Vec3) {

    companion object {
        fun at(a: Vec3, b: Vec3, t: Float): Vec3 {
            val p = a * (1 - t)
            p.plusAssign(b.x * t, b.y * t, b.z * t)
            return p
        }
    }

    fun at(t: Float) = Companion.at(a, b, t)
}