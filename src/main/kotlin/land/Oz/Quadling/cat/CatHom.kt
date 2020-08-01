package land.Oz.Quadling.cat

import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.randomVec3
import physics.NewtonPoint

/**
 * Created by CowardlyLion on 2020/7/31 20:54
 */
class CatHom(
    val source: CatGraph,
    val target: CatGraph,
    center: NewtonPoint = NewtonPoint(p = (source.center.p + target.center.p) / 2),
    points: MutableList<NewtonPoint> = mutableListOf(),
    colors: MutableList<Vec3> = mutableListOf(),
    lines: MutableList<Int> = mutableListOf()
):CatGraph(center, points, colors, lines) {

    class Line(val type: String, val color: Vec3) {
        companion object {
            val basic = Line("basic", Vec3.Companion.fromColor(20, 100, 70))
            val inclusion = Line("inclusion", Vec3.Companion.fromColor(20, 80, 70))

        }
    }

    val lineDesc = mutableListOf<Line>()

    fun addLine(line: Line) {
        lineDesc += line
        val p1 = add(NewtonPoint(p = randomVec3(source.center.p, 0.1F)), line.color)
        val p2 = add(NewtonPoint(p = randomVec3(target.center.p, 0.1F)), line.color)
        addLine(p1, p2)
    }

}