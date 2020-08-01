package land.Oz.Quadling.cat

import glm_.vec3.Vec3
import glm_.vec3.swizzle.xyz
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by CowardlyLion on 2020/7/31 20:24
 */
class CatContext(
    val points: MutableList<CatGraph>,
    val homs: MutableMap<Pair<CatGraph, CatGraph>, CatHom> = mutableMapOf()  //assumption: homs rare
) {

    fun addLine(s: CatGraph, t: CatGraph, line: CatHom.Line) {
        val hom = homs.computeIfAbsent(s to t) { (so, ta) ->
            CatHom(so, ta)
        }
        hom.addLine(line)
    }

    val mutex = Mutex()

    suspend fun data(): Pair<FloatArray, IntArray> {
        return mutex.withLock {

            val data = mutableListOf<Float>()
            val index = mutableListOf<Int>()
            var bias = 0
            points.forEach {
                it.vertexData(data)
                it.indexData(index, bias)
                bias += it.points.size
            }
            homs.values.forEach { hom ->
                hom.vertexData(data)
                hom.indexData(index, bias)
                bias += hom.points.size
            }
            data.toFloatArray() to index.toIntArray()
        }

    }

    suspend fun update() {
        mutex.withLock {
            points.forEach {
                it.update()
            }
            homs.values.forEach { hom->
                    hom.update()
            }

            if (selected.size > maxSelection) {
                val temp = mutableListOf<CatGraph>()
                for (i in (selected.size - maxSelection) until selected.size) {
                    temp += selected[i]
                }
                selected.clear()
                selected.addAll(temp)
            }

            points.forEach {
                for (i in it.colors.indices) {
                    it.colors[i] = green
                }
            }
            for (i in 0 until selected.size) {

                for (j in selected[i].colors.indices) {
                    selected[i].colors[j] = selected_color[i]
                }
            }
        }
    }

    var maxSelection = 2
    var displaySelected = true
    val selected = mutableListOf<CatGraph>()
    val selected_color = mutableListOf<Vec3>()

    init {
        selected_color += red
        selected_color += blue2
        selected_color += blue

    }

    suspend fun select(pos: Vec3, direction: Vec3) {
        mutex.withLock {


            if (points.isEmpty()) return

            val length = direction.length()

            var maxCos = 0F
            lateinit var closestPoint: CatGraph

            for (point in points) {
                val disp = point.center.p - pos
                val dot = disp.dot(direction)
                if (dot <= 0) continue
                val cos = dot / (disp.length() * length)
                if (cos > maxCos) {
                    maxCos = cos
                    closestPoint = point
                }
            }

            if (selected.isEmpty() || selected[selected.lastIndex] != closestPoint) {
                selected += closestPoint
            }
        }
    }


}
