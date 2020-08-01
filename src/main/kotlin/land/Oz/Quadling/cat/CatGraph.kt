package land.Oz.Quadling.cat

import glm_.vec3.Vec3
import physics.*
import kotlin.math.atan

/**
 * Created by CowardlyLion on 2020/7/31 20:27
 */
open class CatGraph(
    val center: NewtonPoint,
    val points: MutableList<NewtonPoint>,
    val colors: MutableList<Vec3>,
    val lines: MutableList<Int>,
    var lineAttractForce: Float = 0.2F
) {

    //structure point (for view)
    //generic point
    //special point

    val extra = mutableMapOf<String, NewtonPoint>()




    /*suspend fun addL(point: NewtonPoint, color: Vec3): Int {
        return mutex.withLock {
            points += point
            colors += color
            points.size - 1
        }
    }
    suspend fun addLineL(from: Int, to: Int) {
        mutex.withLock {
            lines += from
            lines += to
        }
    }*/
    fun add(point: NewtonPoint, color: Vec3): Int {
        points += point
        colors += color
        return points.size - 1
    }

    fun addLine(from: Int, to: Int) {
        lines += from
        lines += to
    }


    //    val mutex = Mutex()
    fun vertexData(data: MutableList<Float>) {
        fun put(vec: Vec3) {
            data += vec.x
            data += vec.y
            data += vec.z
        }

        for (i in points.indices) {
            put(points[i].p)
            put(colors[i])
        }
    }
    fun vertexData(data: MutableList<Float>, rel: Vec3) {
        fun putP(vec: Vec3) {
            data += vec.x + rel.x
            data += vec.y + rel.y
            data += vec.z + rel.z
        }
        fun put(vec: Vec3) {
            data += vec.x
            data += vec.y
            data += vec.z
        }


        for (i in points.indices) {
            putP(points[i].p)
            put(colors[i])
        }
    }

    fun indexData(data: MutableList<Int>, bias: Int){
        lines.forEach {
            data += it + bias
        }
    }

    val attractForce = 0.5F
    val pivotForce = 0.5F

    val extraConstraint = mutableListOf<() -> Unit>()


    open fun update() {
//        mutex.withLock {

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

        for (i in 0 until lines.size step 2) {
            val a = points[lines[i]]
            val b = points[lines[i + 1]]
            val f = hooke(a.p, b.p, 1.0, lineAttractForce)
            a.f.plusAssign(f)
            b.f.plusAssign(-f)
        }

        points.forEach {    //拉近锚点
            val f = force(it.p, center.p) { r -> pivotForce * atan(r) }
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

        extraConstraint.forEach { it.invoke() }


        points.forEach { it.update() }
    }


}
