package physics

import game.main.Univ
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.cycNext
import math.cycPrev
import math.randomColor
import math.vector.distance
import math.vector.distance2
import kotlin.math.atan
import kotlin.math.sqrt

/**
 * Created by CowardlyLion on 2020/7/23 12:27
 */
class Knot(
    base: Vec3,
    var direction: Vec3
) {
    val basePoint = NewtonPoint(p = base)
    val points = mutableListOf<NewtonPoint>()
    var baseColor = randomColor(0.2F)
    var crossColor = randomColor(0.2F)

    var distanceLimit = 0.1F
    var distanceLimit2 = distanceLimit * distanceLimit

    val mutex = Mutex()

    suspend fun tryGenerate(pos: Vec3) {
        mutex.withLock {
            if (points.isEmpty() || distance2(points.last().p, pos) > distanceLimit2) {
                points += NewtonPoint(p = pos)
//                Univ.logger.info {
//                    points.size
//                }
            }
        }
    }

    var range = 1F

    suspend fun calcRange() {
        mutex.withLock {
            range = if (points.isEmpty()) 1F else {
                val maxDist = sqrt(points.map { distance2(basePoint.p, it.p) }.max()!!)
                maxDist + 2
            }
        }
    }


    suspend fun vertexData(): FloatArray {
        if(points.size<=2) return FloatArray(0)
        return mutex.withLock {
            val arr = FloatArray(6 * points.size)

            var index = 0
            fun put(vec: Vec3) {
                arr[index++] = vec.x
                arr[index++] = vec.y
                arr[index++] = vec.z
            }
            points.forEach {
                put(it.p)
                put(baseColor)
            }
            arr
        }
    }
    suspend fun indexData(): IntArray {
        if(points.size<=2) return IntArray(0)
        return mutex.withLock {
            val arr = IntArray(2 * points.size)

            var index = 0
            for (i in 0 until points.size - 1) {
                arr[index++] = i
                arr[index++] = i + 1
            }
            arr[index++] =  points.size - 1
            arr[index++] = 0
            arr
        }
    }

    suspend fun update() {
        if (points.size < 3) return

        points.forEach { it.f = Vec3() }
        for (i in points.indices) { //互相排斥
            for (j in 0 until i) {
                val a = points[i]
                val b = points[j]
                val f = gravity(a.p, a.m, b.p, b.m, 2.0, -0.0005F)
                a.f.plusAssign(f)
                b.f.plusAssign(-f)
            }
        }

        for (i in 0 until points.size) {//拉近连线
            val a = points[i]
            val b = points[next(i)]
            val f = hooke(a.p, b.p, distanceLimit/1F, 1.0, 0.1F)
            a.f.plusAssign(f)
            b.f.plusAssign(-f)
        }

        points.forEach {    //拉近锚点
            val f = hooke(it.p, basePoint.p, 1.0, 0.1F)
            it.f.plusAssign(f)
        }
        points.forEach {    //推离锚点
            val f = force(it.p, basePoint.p) { r -> 0.1F }
            it.f.plusAssign(f)
        }
        for (i in points.indices) { //流体阻力
            val a = points[i]
            a.f.plusAssign(drag.get(a.p, a.v))
        }

        if (angle) {

            for (i in points.indices) {
                val p = points[i]
                val a = points[cycPrev(points.size, i)]
                val b = points[cycNext(points.size, i)]
                val f = angleForce(p.p, a.p, b.p, 0.0001F)
                f.div(f.length())
                a.f.plusAssign(-f)
                b.f.plusAssign(-f)
            }
        }


        mutex.withLock {
            points.forEach { it.update() }
        }

    }

    var angle = false


    fun prev( a: Int) = cycPrev(points.size, a)
    fun next( a: Int) = cycNext(points.size, a)

}