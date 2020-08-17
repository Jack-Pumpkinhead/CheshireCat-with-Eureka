package physics.particles

import physics.NewtonPoint
import physics.hooke

/**
 * Created by CowardlyLion on 2020/8/11 21:55
 */
class LineHooke(
    points: List<NewtonPoint>,
    val lines: MutableList<Int> = mutableListOf(),
    var strenth: Float
):ParticleComponent(points) {

    fun addLine(from: Int, to: Int) {
        lines += from
        lines += to
    }

    override fun update() {
        for (i in 0 until lines.size step 2) {  //拉近连线
            val a = points[lines[i]]
            val b = points[lines[i + 1]]
            val f = hooke(a.p, b.p, 1.0, strenth)
            a.f.plusAssign(f)
            b.f.plusAssign(-f)
        }
    }


}