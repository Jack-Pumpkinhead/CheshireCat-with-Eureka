package math

import kotlin.math.sqrt

/**
 * Created by CowardlyLion on 2020/7/2 11:11
 */

//     /  |
//    /   | y
//   /    |
//  ---x---
fun 斜边(x: Float, y: Float) = sqrt(x*x + y*y)
fun tCos(x: Float, y: Float): Float {
    return x/斜边(x,y)
}
fun tSin(x: Float, y: Float): Float {
    return y/斜边(x,y)
}
