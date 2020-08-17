package physics.particles

import physics.NewtonPoint
import physics.force
import kotlin.math.atan

/**
 * Created by CowardlyLion on 2020/8/11 22:03
 */
class PivotForce(
    points: List<NewtonPoint>,
    var pivot: NewtonPoint,
    var strenth: Float
):ParticleComponent(points) {

    override fun update() {
        points.forEach {    //拉近锚点
            val f = force(it.p, pivot.p) { r -> strenth * atan(r) }
            it.f.plusAssign(f)
        }
    }


}