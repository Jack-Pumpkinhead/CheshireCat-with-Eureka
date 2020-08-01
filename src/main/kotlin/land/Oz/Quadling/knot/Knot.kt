package land.Oz.Quadling.knot

import glm_.mat2x2.Mat2
import glm_.mat2x2.Mat2d
import glm_.vec2.Vec2
import glm_.vec2.Vec2d
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xy
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.cycNext
import math.cycPrev
import math.randomColor
import math.vector.*
import physics.*
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
            for (i in 0 until points.size) {
                arr[index++] = i
                arr[index++] = i.next()
            }
            arr
        }
    }

    val nearPoint = mutableListOf<Vec3>()

    suspend fun nearPointV(): FloatArray {
        return mutex.withLock {
            toVertexData(nearPoint, Vec3.fromColor(100, 20, 60))
        }
    }
    suspend fun nearPointI(): IntArray {
        return mutex.withLock {
            IntArray(nearPoint.size) { it }
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
        mutex.withLock {

            nearPoint.clear()
            for (i in points.indices) {
                for (j in i + 2 until points.size) {
                    if (i == 0 && j == points.size - 1) continue
                    val pa0 = points[i]
                    val pa1 = points[i.next()]
                    val pb0 = points[j]
                    val pb1 = points[j.next()]

                    val a0 = points[i].p
                    val a1 = points[i.next()].p
                    val b0 = points[j].p
                    val b1 = points[j.next()].p


                    val da = a1 - a0
                    val db = b1 - b0
                    val dab0 = a0 - b0
                    val c = -da.dot(db)


                    val A = Mat2(da.length2(), c, c, db.length2())
                    if (A.det == 0F) continue

                    val t = solveLinear(A, Vec2(da.dot(dab0), db.dot(dab0)))

//                val normal = (a1 - a0).crossAssign(b1 - b0)
//                val length2 = normal.length2()
//                if (length2 == 0F) continue

//                normal.plusAssign((b0-a0).dot(normal))
//                normal.divAssign(length2)
//                val a0_ = a0 + normal
//                val a1_ = a1 + normal
                    if (t.x.within01() && t.y.within01()) {
                        val a2 = LineSeg.at(a0, a1, t.x)
                        val b2 = LineSeg.at(b0, b1, t.y)
                        val n = a2 - b2
                        val len = n.length()
                        if (len < 0.1F) {

                            n.divAssign(len * len * 10F)
                            pa0.f.plusAssign(n * (1 - t.x))
                            pa1.f.plusAssign(n * t.x)
                            pb0.f.plusAssign(-n * (1 - t.y))
                            pb1.f.plusAssign(-n * t.y)
                            nearPoint += a2
                            nearPoint += b2
                        }

                    }



                }
            }
        }

        for (i in 0 until points.size) {//拉近连线
            val a = points[i]
            val b = points[i.next()]
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
                val a = points[i.prev()]
                val b = points[i.next()]
                val f = angleForce(p.p, a.p, b.p, 0.0001F)
                f.div(f.length())
                a.f.plusAssign(-f)
                b.f.plusAssign(-f)
            }
        }


        mutex.withLock {
            points.forEach { it.update() }
        }

        findCrossing()

    }

    var angle = false


//    fun prev( a: Int) = cycPrev(points.size, a)
//    fun next( a: Int) = cycNext(points.size, a)
    fun Int.prev() = cycPrev(points.size, this)
    fun Int.next() = cycNext(points.size, this)


    suspend fun findCrossing() {
        if (points.size <= 3) return
        mutex.withLock {
            crossingA.clear()
            crossingB.clear()
            /*Univ.logger.info {
                points.joinToString { it.p.toString() }
            }*/

            for (i in points.indices) {
                for (j in i + 2 until points.size) {
                    processCrossing(i, j)
                }
            }
        }
    }

    val crossingA = mutableListOf<Vec3>()
    val crossingB = mutableListOf<Vec3>()


    var t0 = 0F
    var t1 = 0F


    fun hasCrossing(a: Int, a_: Int, b: Int, b_: Int) = hasCrossing(points[a].p.xy, points[a_].p.xy, points[b].p.xy, points[b_].p.xy)
//    fun hasCrossing(a: Vec3, a_: Vec3, b: Vec3, b_: Vec3) = hasCrossing(a.xy, a_.xy, b.xy, b_.xy)

    fun hasCrossing(a: Vec2, a_: Vec2, b: Vec2, b_: Vec2): Boolean = hasCrossing(Vec2d(a), Vec2d(a_), Vec2d(b), Vec2d(b_))
    fun hasCrossing(a: Vec2d, a_: Vec2d, b: Vec2d, b_: Vec2d): Boolean {
        val ab = b - a
//        val aa_ = a_ - a
//        val bb_ = b_ - b

        val mat = Mat2d(a_.x - a.x, b_.x - b.x, a_.y - a.y, b_.y - b.y)        //按列排满
//        val mat = Mat2(aa_.x, bb_.x, aa_.y, bb_.y)        //按列排满
        val det = mat.det
        if (det == 0.0) {
            return false
        }
        mat.timesAssign(ab)
        ab.divAssign(det)
//        t0 = ab.x.toFloat()
//        t1 = ab.y.toFloat()
       /* Univ.logger.info {
            "t0: ${t0}"
        }
        Univ.logger.info {
            "t1: ${t1}"
        }*/
        if (within01(ab.x) && within01(ab.y)) {

            t0 = ab.x.toFloat()
            t1 = ab.y.toFloat()
            return true
        }
        return false
    }

    fun within01(a: Double) = 0 <= a && a < 1

    fun processCrossing(i: Int, j: Int) {
        if (hasCrossing(i, i.next(), j, j.next())) {
            val a = points[i].p
            val a_ = points[i.next()].p
            val b = points[j].p
            val b_ = points[j.next()].p
            val aa_ = a_ - a
            val bb_ = b_ - b
            aa_.timesAssign(t0)
            aa_.plusAssign(a)
            bb_.timesAssign(t1)
            bb_.plusAssign(b)
            crossingA += aa_
            crossingB += bb_

        }
    }

    suspend fun vertexData_Crossing(): FloatArray {
        return mutex.withLock {
            val arr = FloatArray(crossingA.size * 12)

            var index = 0
            fun put(vec: Vec3) {
                arr[index++] = vec.x
                arr[index++] = vec.y
                arr[index++] = vec.z
            }
            for (i in crossingA.indices) {
                val a = crossingA[i]
                val b = crossingB[i]
                val extrude = if (a.z > b.z) 0.1F else -0.1F
                put(a.plus(0, 0, extrude))
                put(crossColor)
                put(b.plus(0, 0, -extrude))
                put(crossColor)
            }
            arr
        }
    }
    suspend fun indexData_Crossing(): IntArray {
        return mutex.withLock {
            val arr = IntArray(crossingA.size * 2) { it }
            arr
        }
    }

}