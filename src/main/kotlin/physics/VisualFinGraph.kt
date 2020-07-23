package physics

import game.loop.TickableTS
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.graph.FinGraph
import math.randomColor
import math.randomVec3
import kotlin.math.atan

/**
 * Created by CowardlyLion on 2020/7/6 21:02
 */
class VisualFinGraph(
    val graph: FinGraph,
    var pivotEnabled: Boolean = false,
    var pivot: Vec3 = Vec3(),
    var pivotForce: Float = 1F
):TickableTS {

    val points = List(graph.objs){i -> NewtonPoint(p = randomVec3(10F)) }
    val colors = List(graph.arrs){
        val color = randomColor()
        color to color
    }

    suspend fun vertexData(): FloatArray {
        val arr = FloatArray(6 * 2 * graph.arrs)

        mutex.withLock {

            var index = 0
            operator fun FloatArray.plusAssign(vec: Vec3) {
                this[index++] = vec.x
                this[index++] = vec.y
                this[index++] = vec.z
            }

            for (i in 0 until graph.arrs) {
                arr += points[graph.source(i)].p
                arr += colors[i].first
                arr += points[graph.target(i)].p
                arr += colors[i].second
            }
        }
        return arr
    }
    fun indexData(): IntArray = IntArray(2 * graph.arrs) { it }






    val attractForce = 0.5F

    fun mass() = points.map(NewtonPoint::m).sum()
    fun center() = points.map { it.p.times(it.m) }
        .reduce { acc, p_m -> acc.plusAssign(p_m);acc } / mass()


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

        for (arr in 0 until graph.arrs) { //拉近连线
            val a = points[graph.source(arr)]
            val b = points[graph.target(arr)]
            val f = hooke(a.p, b.p, 1.0, 0.1F)
            a.f.plusAssign(f)
            b.f.plusAssign(-f)
        }

        if (pivotEnabled) {
            points.forEach {    //拉近锚点
                val f = force(it.p, pivot) { r -> pivotForce * atan(r) }
                it.f.plusAssign(f)
            }
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


        mutex.withLock {
            points.forEach { it.update() }
        }
    }

    override suspend fun update(tick: Long, timemillis: Long) {
        update()
    }
}