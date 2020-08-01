package math.vector

import glm_.vec1.operators.plusAssign
import glm_.vec3.Vec3
import glm_.vec3.operators.plusAssign
import glm_.vec3.swizzle.xyz

/**
 * Created by CowardlyLion on 2020/7/26 22:46
 */
class Line(val base: Vec3, val pointer: Vec3){

    companion object {

    }

    fun at(t: Float): Vec3 {
        val p = pointer * t
        p.plusAssign(base)
        return p
    }

    fun toBezier(): BezierCurve {
        return BezierCurve(mutableListOf(base.xyz, base + pointer))
    }


}