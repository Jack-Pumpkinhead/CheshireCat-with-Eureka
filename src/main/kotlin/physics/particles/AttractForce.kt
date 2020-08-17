package physics.particles

import physics.NewtonPoint
import physics.force
import kotlin.math.atan

/**
 * Created by CowardlyLion on 2020/8/11 22:05
 */
class AttractForce(
    points: List<NewtonPoint>,
    var strenth: Float
):ParticleComponent(points) {

    override fun update() {
        for (i in points.indices) { //互相拉近
            for (j in 0 until i) {
                val a = points[i]
                val b = points[j]
                val f = force(a.p, b.p) { r -> strenth * atan(r) }
                a.f.plusAssign(f)
                b.f.plusAssign(-f)
            }
        }
    }


}