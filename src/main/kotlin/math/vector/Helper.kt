package math.vector

import glm_.mat2x2.Mat2
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xx
import glm_.vec3.swizzle.xy
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
fun distance(a: Vec3, b: Vec3): Float = (a - b).length()

/**
 * determinant of a->a_ and b->b_
 * */
fun crossDeterminant(a: Vec2, a_: Vec2, b: Vec2, b_: Vec2): Float {
    return Mat2(a_ - a, b_ - b).det
}

fun solveLinear(A: Mat2, b: Vec2): Vec2 {
    return A.inverse().times(b)
}

fun Float.within01(delta: Float = 0.001F) = 0 - delta <= this && this < 1 + delta

fun toVertexData(points: List<Vec3>, color: Vec3): FloatArray {
    val arr = FloatArray(6 * points.size)

    var index = 0
    fun put(vec: Vec3) {
        arr[index++] = vec.x
        arr[index++] = vec.y
        arr[index++] = vec.z
    }
    points.forEach {
        put(it)
        put(color)
    }
    return arr
}

fun MutableList<Float>.addVec(vec: Vec3) {
    add(vec.x)
    add(vec.y)
    add(vec.z)
}
fun MutableList<Float>.addVecRel(vec: Vec3, rel: Vec3) {
    add(vec.x + rel.x)
    add(vec.y + rel.y)
    add(vec.z + rel.z)
}

fun projection(vec: Vec3, to: Vec3): Vec3 {
    val dot = vec.dot(to)
    if (dot == 0F) return Vec3()
    return to.times(dot / to.length2())
}
fun dotUnit(vec: Vec3, to: Vec3): Float {
    val dot = vec.dot(to)
    if (dot == 0F) return 0F
    return dot / to.length()
}