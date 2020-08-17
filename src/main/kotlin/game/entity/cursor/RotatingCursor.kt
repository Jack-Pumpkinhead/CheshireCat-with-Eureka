package game.entity.cursor

import game.entity.DataCell
import game.entity.LineString
import game.entity.Lines
import game.main.Univ
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xyz
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import land.Oz.Quadling.cat.*
import math.randomColor
import math.randomVec3
import physics.NewtonPoint
import physics.hooke
import physics.staticDrag

/**
 * Created by CowardlyLion on 2020/8/17 19:52
 */
class RotatingCursor(
    val center: NewtonPoint
) {

    val rotCursors = mutableListOf<NewtonPoint>()

//    val rotColors = mutableListOf<Vec3>(purple, blue2, red)
    val rotColors = mutableListOf<Vec3>()
    val rotDatas = mutableListOf<LineString>()
    /*val rotDatas = CatGraph4(
        boundary = CatPoint4(NewtonPoint()),
        objs = mutableListOf()
    )*/

    fun add(center: Vec3) {
        rotCursors += NewtonPoint(
            p = center + randomVec3(0.1F),
            v = randomVec3(0.01F)
        )
        rotColors += randomColor(0.1F)
        rotDatas += LineString()
    }


    fun data(): Pair<FloatArray, IntArray> {
        val data = DataCell()

        rotDatas.forEach { data.fill(it) }

        return data.toArray()
    }

    val mutex = Mutex()

    fun update() {



        val rotCursor = rotCursors[0]
            rotCursor.f.put(0, 0, 0)
            rotCursor.f.plusAssign(hooke(rotCursor.p, center.p, 2.0, 100F))
            rotCursor.f.plusAssign(staticDrag(rotCursor.p, rotCursor.v, Vec3(), 1.0, 1F))
            rotCursor.update()
        val rotCursor1 = rotCursors[1]
            rotCursor1.f.put(0, 0, 0)
            rotCursor1.f.plusAssign(hooke(rotCursor1.p, rotCursor.p, 2.0, 100F))
            rotCursor1.f.plusAssign(rotCursor.v.cross(rotCursor1.v).times(3F))
            rotCursor1.f.plusAssign(staticDrag(rotCursor1.p, rotCursor1.v, Vec3(), 1.0, 0.2F))
            rotCursor1.update()
        val rotCursor2 = rotCursors[2]
            rotCursor2.f.put(0, 0, 0)
            rotCursor2.f.plusAssign(hooke(rotCursor2.p, rotCursor.p, 2.0, 100F))
            rotCursor2.f.plusAssign(rotCursor2.v.cross(rotCursor.v).times(4F))
            rotCursor2.f.plusAssign(staticDrag(rotCursor2.p, rotCursor2.v, Vec3(), 1.0, 0.3F))
            rotCursor2.update()

        rotCursors.forEach {
            if (it.v.length() > 100F) {
//                it.v.timesAssign(0.99F)
                it.v.put(0F, 0F, 0F)
                it.p.put(rotCursor.p)

            }
        }

        for (i in rotDatas.indices) {

            val line = rotDatas[i]
            val p = line.add(
                point = NewtonPoint(
//                            p = fpv.forward(1F),
                    p = rotCursors[i].p.xyz
                ),
                color = rotColors[i]
            )
//            Univ.logger.info {
//                "line size: ${line.points.size}"
//            }

            while (line.points.size > 37) {


                line.points.removeAt(0)
            }


        }


    }



}