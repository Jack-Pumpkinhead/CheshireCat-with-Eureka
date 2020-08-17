package game.entity

/**
 * Created by CowardlyLion on 2020/8/13 12:06
 */
class DataCell(
    val data: MutableList<Float> = mutableListOf(),
    val index: MutableList<Int> = mutableListOf(),
    var bias: Int = 0
) {

    fun fill(line: Lines) {
        line.vertexData(data)
        line.indexData(index, bias)
        bias += line.points.size
    }
    fun fill(line: LineString) {
        line.vertexData(data)
        line.indexData(index, bias)
        bias += line.points.size
    }


    fun toArray(): Pair<FloatArray, IntArray> {
        return data.toFloatArray() to index.toIntArray()
    }

}