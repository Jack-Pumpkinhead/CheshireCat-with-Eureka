package math

import glm_.vec3.Vec3

/**
 * Created by CowardlyLion on 2020/7/14 23:43
 */

fun List<Float>.minIndex(): Int {
    if (isEmpty()) return -1
    else {
        var minI = 0
        var min = get(0)
        for (i in 1 until size) {
            if (get(i) < min) {
                minI = i
                min = get(i)
            }
        }
        return minI
    }
}
fun half(a: Int) = if (a > 1) a / 2 else 1

/**
* calculi on cyclic group Z/(mod)
* @param mod >= 1
* @param a in [0, mod)
* */
fun cycPrev(mod: Int, a: Int): Int {
    return if (a == 0) {
        mod - 1
    } else a - 1
}
/**
 * calculi on cyclic group Z/(mod)
 * @param mod >= 1
 * @param a in [0, mod)
 * */
fun cycNext(mod: Int, a: Int): Int {
    return if (a == mod - 1) {
        0
    } else a + 1
}