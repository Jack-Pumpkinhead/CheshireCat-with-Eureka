package physics

import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.randomColor
import math.randomVec3
import physics.graph.Line
import kotlin.math.atan


/**
 * Created by CowardlyLion on 2020/7/13 15:13
 */
class VisualGraph(
    val pivot: NewtonPoint,
    var pivotForce: Float = 1F,
    numPoints: Int,
    val points: MutableList<NewtonPoint> = MutableList(numPoints) { i ->
        NewtonPoint(p = randomVec3(pivot.p, 10F))
    },
    val arrows: MutableList<Line> = mutableListOf() //暂时用Line
) {

    fun addObj(point: NewtonPoint = NewtonPoint(p = randomVec3(pivot.p, 10F))) {
        points += point
    }

    fun removeObj(i: Int) {
        val point = points.removeAt(i)
        arrows.removeIf { it.src == point || it.tar == point }
    }

    fun addArr(src: Int, tar: Int, srcColor: Vec3 = randomColor(0.1F), tarColor: Vec3 = srcColor) {
        arrows += Line(points[src], points[tar], srcColor, tarColor)
    }

    val attractForce = 0.5F
    val g = 0.98F

    fun mass() = points.map(NewtonPoint::m).sum()
    fun center() = points.map { it.p.times(it.m) }
        .reduce { acc, pm -> acc.plusAssign(pm);acc } / mass()


    val mutex = Mutex()

    suspend fun update() {
        points.forEach { it.f = Vec3() }
        for (i in points.indices) { //互相排斥
            for (j in 0 until i) {
                val a = points[i]
                val b = points[j]
                val f = gravity(a.p, a.m, b.p, b.m, 1.0, -0.5F)
                a.f.plusAssign(f)
                b.f.plusAssign(-f)
            }
        }

        for (arr in arrows) { //拉近连线
            val a = arr.src
            val b = arr.tar
            val f = hooke(a.p, b.p, 1.0, 0.1F)
            a.f.plusAssign(f)
            b.f.plusAssign(-f)
        }

//        pivot.update()
        points.forEach {    //拉近锚点
            val f = force(it.p, pivot.p) { r -> pivotForce * atan(r) }
            it.f.plusAssign(f)
        }


        for (i in points.indices) { //互相拉近
            for (j in 0 until i) {
                val a = points[i]
                val b = points[j]
                val f = force(a.p, b.p) { r -> attractForce * atan(r) }
                a.f.plusAssign(f)
                b.f.plusAssign(-f)
            }
        }
        for (i in points.indices) { //流体阻力
            val a = points[i]
            a.f.plusAssign(drag.get(a.p, a.v))
        }

        for (i in points.indices) { //重力
            val a = points[i]
            a.f.plusAssign(0, a.m * g, 0)
        }




        mutex.withLock {
            points.forEach { it.update() }
        }
    }



}