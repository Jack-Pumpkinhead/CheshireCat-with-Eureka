package physics

import glm_.vec3.Vec3
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by CowardlyLion on 2020/6/29 17:26
 */
class DragForce(
    var fluidMassDensity: (Vec3) -> Float,
    var v_fluid: (Vec3) -> Vec3,
    var coefficient: () -> Float
) {

    fun get(p: Vec3, v: Vec3): Vec3 {
        val dv = v_fluid(p) - v
        val r2 = dv.length2()
        if (r2 == 0f) return dv
        val f = fluidMassDensity(p) *
                r2.toDouble().pow(0.5).toFloat() *
                coefficient() //Ignore area coefficient.
        return dv * (f / sqrt(r2))
    }


}