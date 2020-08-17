package physics.particles

import math.vector.distance
import physics.NewtonPoint
import physics.force
import physics.hooke
import kotlin.math.atan

/**
 * Created by CowardlyLion on 2020/8/12 12:07
 */
class KeepRalative(
    points: List<NewtonPoint>,
    val percentage: MutableList<Float> = mutableListOf(),  //0..1 from a to b
    var strenth: Float = 1.0F
):ParticleComponent(points) {

    override fun update() {
        for (i in percentage.indices) {
            val pa = points[3 * i + 0]
            val pb = points[3 * i + 2]
            val pm = points[3 * i + 1]
            pm.f.plusAssign(force(pm.p, pb.p) { r -> strenth * atan(r) })
            pm.f.plusAssign(hooke(pm.p, pa.p, strenth * distance(pa.p, pb.p) * percentage[i]))
        }
    }


}