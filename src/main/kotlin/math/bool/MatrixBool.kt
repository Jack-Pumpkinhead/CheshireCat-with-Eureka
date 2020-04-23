package math.bool

/**
 * Created by CowardlyLion on 2020/4/20 16:27
 */
open class MatrixBool(size: Int = 64, var default: Boolean = false) {
    var size = size
        set(value) {
            m = Array(value) { i -> Array(value) { j -> if (i < field && j < field) m[i][j] else default } }
            field = value
        }


    var m = Array(size) { Array(size) { default } }
    operator fun get(x: Int) = m[x]





}