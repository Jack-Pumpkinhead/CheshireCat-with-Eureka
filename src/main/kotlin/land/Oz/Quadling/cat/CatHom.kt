package land.Oz.Quadling.cat

import glm_.vec3.Vec3
import math.randomVec3
import math.vector.distance
import physics.NewtonPoint
import physics.drag
import physics.force
import physics.hooke
import kotlin.math.atan

/**
 * Created by CowardlyLion on 2020/7/31 20:54
 */
class CatHom(
    val source: CatPoint,
    val target: CatPoint,
    center: NewtonPoint = NewtonPoint(p = (source.center.p + target.center.p) / 2),
    points: MutableList<NewtonPoint> = mutableListOf(),
    colors: MutableList<Vec3> = mutableListOf(),
    lines: MutableList<Int> = mutableListOf()
):CatPoint(center, points, colors, lines) {

    class Line(val type: String, val color: Vec3) {
        companion object {
            val basic = Line("basic", Vec3.Companion.fromColor(20, 100, 70))
            val basic2 = Line("basic", blue)
            val inclusion = Line("inclusion", Vec3.Companion.fromColor(20, 80, 70))
            val spectial = Line("spectial", Vec3.Companion.fromColor(20, 80, 70))

        }
    }

    val lineDesc = mutableListOf<Line>()

    init {
//        val p = NewtonPoint(p = randomVec3(source.center.p, 0.1F))
//        add(p, Line.basic.color)
//        add(p, Line.inclusion.color)
    }

    val shortDist = 0.1F
    val shortshortDist = 0.05F
    val shortDistRatio = 0.1F

    fun addLine(line: Line) {
        lineDesc += line
        val point1 = NewtonPoint(p = randomVec3(source.center.p, 0.1F))
//        val p1 = add(point1, line.color)
        val point2 = NewtonPoint(p = randomVec3(target.center.p, 0.1F))
        val disp = point1.p - point2.p
        disp.divAssign(disp.length())
        disp.timesAssign(shortDist)
        val point3 = NewtonPoint(p = point2.p + disp)
//        val p3 = add(point3, line.color)
//        val p3_ = add(point3, blue) //duplicate point (pos) cause error force
//        val p2 = add(point2, blue)

//        extraConstraint += {
//            point1.f.plusAssign(force(point1.p, source.center.p) { it })
//            point2.f.plusAssign(force(point2.p, target.center.p) { it })
//            point3.f.plusAssign(hooke(point3.p, point2.p, shortDist))
//        }

//        source.add(point1, green)
//        target.add(point2, blue)
        val p1 = add(point1, green)
        val p3 = add(point3, green)
        val p3_ = add(point3, blue)
        val p2 = add(point2, blue)
        active += true
        active += true
        active += false
        active += true

        extraConstraint += {
            point1.f.plusAssign(hooke(point1.p, source.center.p,shortshortDist))
            point2.f.plusAssign(hooke(point2.p, target.center.p,shortshortDist))
            point3.f.plusAssign(force(point3.p, point1.p) { r -> pivotForce * atan(r) })
            point3.f.plusAssign(hooke(point3.p, point2.p, distance(point1.p, point2.p) * shortDistRatio))

        }


        addLine(p1, p3)
        addLine(p3_, p2)
    }

    val active = mutableListOf<Boolean>()

    override fun update() {
        points.filterIndexed { index, _ -> active[index]}.forEach {
            it.f = Vec3()
        }
        extraConstraint.forEach { it.invoke() }

        points.filterIndexed { index, _ -> active[index]}.forEach {
            it.f.plusAssign(drag.get(it.p, it.v))
            it.update()
        }
    }
}