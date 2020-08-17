package land.Oz.Quadling.cat

import game.entity.DataCell
import game.main.Univ
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by CowardlyLion on 2020/8/12 11:56
 */
class CatGraph4(
    val boundary: CatPoint4,
    val objs: MutableList<CatPoint4>
) {
    val homs: MutableList<CatHom4> = mutableListOf()

    var boundaryVisible = true

    fun addObj(obj: CatPoint4) {
        objs += obj
    }

    fun addLine(s:CatPoint4, t: CatPoint4) {
        val hom = CatHom4(s, t)
        hom.addArr()
        homs += hom
        s.under += hom
        t.over += hom
    }

    val mutex = Mutex()

    suspend fun data(): Pair<FloatArray, IntArray>{
        return mutex.withLock {

            val data = DataCell()
            objs.forEach { data.fill(it.line) }
            homs.forEach { data.fill(it.line) }

            if (boundaryVisible) {
                data.fill(boundary.line)
            }

            data.toArray()
        }

    }

    suspend fun update() {
        mutex.withLock {

            objs.forEach {
                it.update()
            }
            homs.forEach {
                it.update()
            }


        }
    }

    var maxSelection = 2
    var displaySelected = true
    val selected = mutableListOf<CatPoint4>()
    val selected_color = mutableListOf(
        red, blue2, blue
    )

    fun select(pos: Vec3, direction: Vec3) {
        if (objs.isEmpty()) return

        val length = direction.length()

        var maxCos = 0F
        var closestPoint: CatPoint4? = null

        for (obj in objs) {
            val disp = obj.center.p - pos
            val dot = disp.dot(direction)
            if (dot <= 0) continue
            val cos = dot / (disp.length() * length)
            if (cos > maxCos) {
                maxCos = cos
                closestPoint = obj
            }
        }

        if (closestPoint != null &&
            (selected.isEmpty() || selected[selected.lastIndex] != closestPoint)
        ) {
            selected += closestPoint!!
        }
    }

}