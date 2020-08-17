package physics.particles

import glm_.vec3.Vec3
import physics.NewtonPoint
import physics.dragWeak
import physics.staticDrag

/**
 * Created by CowardlyLion on 2020/8/11 22:06
 */
class StaticDrag(
    points: List<NewtonPoint>,
    var strenth: Float
):ParticleComponent(points) {

    override fun update() {
        for (i in points.indices) { //流体阻力
            val a = points[i]
            a.f.plusAssign(staticDrag(a.p, a.v, Vec3(), 1.0, strenth))
        }
    }


}