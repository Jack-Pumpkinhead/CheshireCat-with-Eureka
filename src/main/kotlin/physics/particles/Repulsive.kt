package physics.particles

import physics.NewtonPoint
import physics.gravity

/**
 * Created by CowardlyLion on 2020/8/11 21:51
 */
class Repulsive(
    points: List<NewtonPoint>,
    var strenth: Float
):ParticleComponent(points) {

    override fun update() {

        for (i in points.indices) { //互相排斥
            for (j in 0 until i) {
                val a = points[i]
                val b = points[j]
                val f = gravity(a.p, a.m, b.p, b.m, 1.0, -strenth)
                a.f.plusAssign(f)
                b.f.plusAssign(-f)
            }
        }

    }


}