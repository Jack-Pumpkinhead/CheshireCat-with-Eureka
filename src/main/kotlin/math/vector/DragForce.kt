package math.vector

import glm_.vec3.Vec3
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by CowardlyLion on 2020/6/29 17:26
 */
class DragForce(
    var fluidMassDensity: (Vec3) -> Float,
    var fluidSpeed: (Vec3) -> Vec3,
    var coefficient: () -> Float
) {

    fun get(pos: Vec3, speed: Vec3): Vec3 {
        val deltaSpeed = fluidSpeed(pos) - speed
        val lenth2 = deltaSpeed.length2()
        if (lenth2 == 0f) return deltaSpeed
        val f = fluidMassDensity(pos) *
                lenth2.toDouble().pow(0.5).toFloat() *
                coefficient() //Ignore area coefficient.
        return deltaSpeed * (f / sqrt(lenth2))
    }


}