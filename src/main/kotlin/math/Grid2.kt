package math

/**
 * Created by CowardlyLion on 2020/7/26 11:08
 */
class Grid2(
    var nx: Int,
    var ny: Int,
    var dx: Float,
    var dy: Float,
    var ox: Float,
    var oy: Float,
    var scaleZ: (Float) -> Float = { it }
) {

    val value = Array(nx) { FloatArray(ny) }


}