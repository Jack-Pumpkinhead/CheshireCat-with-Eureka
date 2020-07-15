package math

/**
 * Created by CowardlyLion on 2020/7/14 23:43
 */

fun List<Float>.minIndex(): Int {
    if (isEmpty()) return -1
    else {
        var minI = 0
        var min = get(0)
        for (i in 1 until size) {
            if (get(i) < min) {
                minI = i
                min = get(i)
            }
        }
        return minI
    }
}