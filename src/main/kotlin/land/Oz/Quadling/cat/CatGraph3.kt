package land.Oz.Quadling.cat

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


/**
 * Created by CowardlyLion on 2020/8/8 20:40
 */
class CatGraph3(
    val boundary: CatPoint,
    val points: MutableList<CatPoint3>,
    val homs: MutableMap<Pair<CatPoint3, CatPoint3>, MutableList<CatArr3>> = mutableMapOf()  //assumption: homs rare
){

    fun addLine(s: CatPoint3, t: CatPoint3) {
        val hom = homs.computeIfAbsent(s to t) { (_, _) -> mutableListOf() }
        hom += arrOf(s, t)
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
                hom.forEach {
                    it.vertexData(data)
                    it.indexData(index, bias)
                    bias += it.points.size
                }
            }


            boundary.vertexData(data)
            boundary.indexData(index, bias)
            bias += boundary.points.size

            data.toFloatArray() to index.toIntArray()
        }

    }


    suspend fun update() {
        mutex.withLock {

            points.forEach {
                it.update()
            }
            homs.values.forEach { hom->
                hom.forEach {
                    it.update()
                }
            }


        }
    }

}