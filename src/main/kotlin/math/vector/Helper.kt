package math.vector

import glm_.vec3.Vec3
import org.joml.Vector3f

/**
 * Created by CowardlyLion on 2020/6/28 11:25
 */

val halfPI = (Math.PI / 2).toFloat()

fun Vec3.clampX(abs: Float): Vec3 {
    if (x > abs) {
        x = abs
    } else if (x < -abs) {
        x = -abs
    }
    return this
}
fun Vec3.clampY(abs: Float): Vec3 {
    if (y > abs) {
        y = abs
    } else if (y < -abs) {
        y = -abs
    }
    return this
}
fun Vec3.clampZ(abs: Float): Vec3 {
    if (z > abs) {
        z = abs
    } else if (z < -abs) {
        z = -abs
    }
    return this
}

fun distance2(a: Vec3, b: Vec3): Float = (a - b).length2()
