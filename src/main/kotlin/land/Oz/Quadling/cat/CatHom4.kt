package land.Oz.Quadling.cat

import game.entity.Lines
import math.randomVec3
import physics.NewtonPoint
import physics.particles.KeepRalative

/**
 * Created by CowardlyLion on 2020/8/12 12:00
 */
class CatHom4(
    val source: CatPoint4,
    val target: CatPoint4,
    val line: Lines = Lines()
) {

    val rels = mutableListOf<NewtonPoint>()
    val keepRelative = KeepRalative(rels, mutableListOf())

    init {
        line.components += keepRelative
    }

    fun addArr() {

        val point1 = NewtonPoint(p = randomVec3(source.center.p, 0.1F))
        val point2 = NewtonPoint(p = randomVec3(target.center.p, 0.1F))
        val point3 = NewtonPoint(p = (point1.p + point2.p) / 2)

        line.add(point1, green)
        line.add(point3, green)
        line.add(point3, blue)
        line.add(point2, blue)

        rels += point1
        rels += point3
        rels += point2

        keepRelative.percentage += 0.9F



    }

    fun update() {
        line.update()

    }


}