package land.Oz.Quadling.cat

import glm_.vec3.Vec3
import math.vector.addVec
import math.vector.addVecRel
import math.vector.distance
import physics.NewtonPoint
import physics.drag
import physics.force
import physics.hooke
import kotlin.math.atan

/**
 * Created by CowardlyLion on 2020/8/8 22:02
 */
class CatArr3(
    val source: CatPoint3,
    val target: CatPoint3,
    val points: MutableList<NewtonPoint> = mutableListOf(),
    val colors: MutableList<Vec3> = mutableListOf(),
    val actives: MutableList<Boolean> = mutableListOf(),
    val lines: MutableList<Int> = mutableListOf()
){

    fun add(point: NewtonPoint, color: Vec3, active: Boolean): Int {
        points += point
        colors += color
        actives += active
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


    val extraConstraint = mutableListOf<(CatArr3, List<NewtonPoint>) -> Unit>()

    val vDist = 0.05F
    val mRatio = 0.1F
    val pivotForce = 0.5F

    fun update() {
        val active = points.filterIndexed { index, _ -> actives[index] }
        active.forEach {
            it.f = Vec3()
        }


        val pa = points[0]
        val pb = points[3]
        val pm = points[1]

        pa.f.plusAssign(hooke(pa.p, source.center.p, vDist))
        pb.f.plusAssign(hooke(pb.p, target.center.p, vDist))
        pm.f.plusAssign(force(pm.p, pa.p) { r -> pivotForce * atan(r) })
        pm.f.plusAssign(hooke(pm.p, pb.p, distance(pa.p, pb.p) * mRatio))


        extraConstraint.forEach { it.invoke(this, active) }

        active.forEach {
            it.f.plusAssign(drag.get(it.p, it.v))
        }

        active.forEach {
            it.update()
        }
    }



}