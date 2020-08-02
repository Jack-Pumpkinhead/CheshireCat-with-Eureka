package land.Oz.Quadling.cat

import glm_.vec3.Vec3
import math.vector.addVec
import math.vector.addVecRel
import physics.*
import kotlin.math.atan

/**
 * Created by CowardlyLion on 2020/8/2 20:54
 */
class CatPoint2(
    val center: NewtonPoint,
    val points: MutableList<NewtonPoint>,
    val colors: MutableList<Vec3>,
    val active: MutableList<Boolean>,
    val lines: MutableList<Int>,
    var lineDist: Float = 1F
) {

    fun add(point: NewtonPoint, color: Vec3): Int {
        points += point
        colors += color
        return points.size - 1
    }

    fun addLine(from: Int, to: Int) {
        lines += from
        lines += to
    }

    fun vertexData(data: MutableList<Float>) {
        for (i in points.indices) {
            data.addVec(points[i].p)
            data.addVec(colors[i])
        }
    }
    fun vertexData(data: MutableList<Float>, rel: Vec3) {
        for (i in points.indices) {
            data.addVecRel(points[i].p, rel)
            data.addVec(colors[i])
        }
    }
    fun indexData(data: MutableList<Int>, bias: Int){
        lines.forEach {
            data += it + bias
        }
    }
    val attractForce = 0.5F
    val pivotForce = 0.5F

    val extraConstraint = mutableListOf<CatPoint2.(List<NewtonPoint>) -> Unit>()

    open fun update() {

        val pointsA = points.filterIndexed { i, _ -> active[i] }

        pointsA.forEach { it.f = Vec3() }
        for (i in pointsA.indices) { //互相排斥
            for (j in 0 until i) {
                val a = pointsA[i]
                val b = pointsA[j]
                val f = gravity(a.p, a.m, b.p, b.m, 1.0, -0.25F * lineDist)
                a.f.plusAssign(f)
                b.f.plusAssign(-f)
            }
        }

        for (i in 0 until lines.size step 2) {  //拉近连线
            if (!active[lines[i]] || !active[lines[i + 1]]) continue
            val a = points[lines[i]]
            val b = points[lines[i + 1]]
            val f = hooke(a.p, b.p, 1.0, 0.1F)
            a.f.plusAssign(f)
            b.f.plusAssign(-f)
        }

        pointsA.forEach {    //拉近锚点
            val f = force(it.p, center.p) { r -> pivotForce * atan(r) }
            it.f.plusAssign(f)
        }

        /*for (i in pointsA.indices) { //互相拉近
            for (j in 0 until i) {
                val a = pointsA[i]
                val b = pointsA[j]
                val f = force(a.p, b.p) { r -> attractForce * atan(r) }
                a.f.plusAssign(f)
                b.f.plusAssign(-f)
            }
        }*/

        for (i in pointsA.indices) { //流体阻力
            val a = pointsA[i]
            a.f.plusAssign(dragWeak.get(a.p, a.v))
        }

        extraConstraint.forEach { it.invoke(this, pointsA) }



        pointsA.forEach { it.update() }
    }
}