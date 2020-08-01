package math.vector

import game.main.Univ
import glm_.vec3.Vec3
import math.linearCombination

/**
 * Created by CowardlyLion on 2020/7/26 22:49
 */
class BezierCurve(val points: MutableList<Vec3>){

    fun at(t: Float): Vec3 {
        when {
            points.isEmpty() -> {
                Univ.logger.warn {
                    "try to interpolate with empty points"
                }
                return Vec3()
            }
            points.size == 1 -> {
                return points[0]
            }
            else -> {

                val s = 1 - t
                val mid = Array(points.size - 1) { i ->
                    linearCombination(points[i], s, points[i + 1], t)
                }
                var j = mid.size - 1
                while (j != 0) {
                    for (i in 0 until j) {
                        mid[i].timesAssign(s)
                        val b = mid[i + 1]
                        mid[i].plusAssign(b.x * t, b.y * t, b.z * t)
                    }
                    j--
                }
                return mid[0]
            }
        }

    }




}