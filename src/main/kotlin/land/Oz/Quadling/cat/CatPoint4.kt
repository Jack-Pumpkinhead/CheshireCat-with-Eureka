package land.Oz.Quadling.cat

import game.entity.Lines
import physics.NewtonPoint
import physics.particles.*

/**
 * Created by CowardlyLion on 2020/8/11 23:00
 */
class CatPoint4(
    val center: NewtonPoint,
    val line: Lines = Lines(),
    val over: MutableList<CatHom4> = mutableListOf(),
    val under: MutableList<CatHom4> = mutableListOf()
) {

    val repulsive = Repulsive(line.points, 0.25F)
    val lineHooke = LineHooke(line.points, line.lines, 0.1F)
    val pivotForce = PivotForce(line.points, center, 0.5F)
    val staticDrag = StaticDrag(line.points, 0.1F)

    init {

        line.components += repulsive
        line.components += lineHooke
        line.components += pivotForce
        line.components += staticDrag


    }

    fun update() {
        line.update()

    }
}