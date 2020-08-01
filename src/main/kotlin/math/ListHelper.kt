package math

import glm_.vec3.Vec3
import physics.NewtonPoint

/**
 * Created by CowardlyLion on 2020/7/31 20:30
 */
fun joinToArray(a: List<Vec3>, b: List<Vec3>): FloatArray {
    val arr = FloatArray(6 * a.size)
    var index = 0
    operator fun FloatArray.plusAssign(vec: Vec3) {
        this[index++] = vec.x
        this[index++] = vec.y
        this[index++] = vec.z
    }

    for (i in a.indices) {
        arr += a[i]
        arr += b[i]
    }
    return arr
}
fun joinToArray1(a: List<NewtonPoint>, b: List<Vec3>): FloatArray {
    val arr = FloatArray(6 * a.size)
    var index = 0
    operator fun FloatArray.plusAssign(vec: Vec3) {
        this[index++] = vec.x
        this[index++] = vec.y
        this[index++] = vec.z
    }

    for (i in a.indices) {
        arr += a[i].p
        arr += b[i]
    }
    return arr
}
