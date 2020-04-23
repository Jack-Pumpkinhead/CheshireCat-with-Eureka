package math.int

import org.joml.Matrix4f

/**
 * Created by CowardlyLion on 2020/4/20 16:25
 */
class MatrixInt(size: Int = 64, var default: Int) {
    var size = size
        set(value) {
            m = Array(value) { i -> Array(value) { j -> if (i < field && j < field) m[i][j] else default } }
            field = value
        }


    var m = Array(size) { Array(size) { 0 } }






}