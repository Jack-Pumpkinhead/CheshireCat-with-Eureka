package land.Oz.Quadling.quad

import game.main.Univ
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xyz
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import land.Oz.Quadling.cat.*
import math.vector.projection
import math.vector.dotUnit
import physics.NewtonPoint
import physics.snapToPlane
import kotlin.math.abs
import kotlin.math.max

/**
 * Created by CowardlyLion on 2020/8/2 21:15
 */
class Squares(
    val center: Vec3,
    val right: Vec3,
    val face: Vec3,
    val down: Vec3 = right.cross(face)
) {

    val squares = mutableListOf<CatPoint2>()

    fun calcCenter() {
        val c = center.xyz
        for (square in squares) {
            square.center.p.put(c)
            c.plusAssign(right)
        }
    }

    fun addSquare() {

        val square = if (squares.isEmpty()) {
            squareLessExact(center)
        } else {
            squareLessExact(squares[squares.lastIndex].center.p + right)
        }

        square.extraConstraint += { pointA: List<NewtonPoint> ->
            for (point in pointA) {
                point.f.plusAssign(
                    snapToPlane(point.p, square.center.p, face, 1F)
                )
            }

            var distFace = 0F
            for (i in 0 until points.size - 1) {
                val disp = points[i].p - points[i + 1].p
                distFace = max(distFace, abs(dotUnit(disp, face)))
            }

            if (distFace < 0.1F) {

                findAlign(this, right)
                findAlign(this, down)

            }


        }
        squares += square
    }

    fun findAlign(cat: CatPoint2, direction: Vec3) {
        var dotRight = 0F
        var pa = -1
        var pb = -1
        for (i in 0 until cat.points.size - 1) {
            val disp = cat.points[i].p - cat.points[i + 1].p
            val dot = dotUnit(disp, direction)
            if (dot < 0) {
                if (dotRight < -dot) {
                    dotRight = -dot
                    pa = i + 1
                    pb = i
                }
            } else {
                if (dotRight < dot) {
                    dotRight = dot
                    pa = i
                    pb = i + 1
                }
            }
        }

        if (dotRight > 0F) {
            val a = cat.points[pa]
            val b = cat.points[pb]
            val disp = b.p - a.p

            val proj = projection(disp, direction)
            proj.minusAssign(disp)
            proj.timesAssign(1F)
            b.f.plusAssign(proj)
        }
    }

    val mutex = Mutex()

    suspend fun data(): Pair<FloatArray, IntArray> {
        return mutex.withLock {

            val data = mutableListOf<Float>()
            val index = mutableListOf<Int>()
            var bias = 0
            squares.forEach {
                it.vertexData(data)
                it.indexData(index, bias)
                bias += it.points.size
            }

            data.toFloatArray() to index.toIntArray()
        }

    }

    suspend fun update() {
        mutex.withLock {



            squares.forEach {
                it.update()
            }


            if (selected.size > maxSelection) {
                val temp = mutableListOf<CatPoint2>()
                for (i in (selected.size - maxSelection) until selected.size) {
                    temp += selected[i]
                }
                selected.clear()
                selected.addAll(temp)
            }

            squares.forEach {
                for (i in it.colors.indices) {
                    it.colors[i] = green
                }
            }
            for (i in 0 until selected.size) {

                for (j in selected[i].colors.indices) {
                    selected[i].colors[j] = selected_color[i]
                }
            }

        }
    }


    var maxSelection = 2
    var displaySelected = true
    val selected = mutableListOf<CatPoint2>()
    val selected_color = mutableListOf(
        red, blue2, blue
    )


    suspend fun select(pos: Vec3, direction: Vec3) {
        mutex.withLock {


            if (squares.isEmpty()) return

            val length = direction.length()

            var maxCos = 0F
            var closestPoint: CatPoint2? = null

            for (point in squares) {
                val disp = point.center.p - pos
                val dot = disp.dot(direction)
                if (dot <= 0) continue
                val cos = dot / (disp.length() * length)
                if (cos > maxCos) {
                    maxCos = cos
                    closestPoint = point
                }
            }

            if (closestPoint != null &&
                (selected.isEmpty() || selected[selected.lastIndex] != closestPoint)
            ) {
                Univ.logger.info {
                    "clo $closestPoint"
                }
                selected += closestPoint!!
            }
        }
    }




}