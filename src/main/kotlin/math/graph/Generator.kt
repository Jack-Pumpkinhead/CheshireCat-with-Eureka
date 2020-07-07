package math.graph

import kotlin.random.Random

/**
 * Created by CowardlyLion on 2020/7/7 11:38
 */

fun randomFinGraph(objs: Int, arrs: Int, noLoop: Boolean = true): FinGraph {
    val s = IntArray(arrs)
    val t = IntArray(arrs)
    if (noLoop) {
        for (i in 0 until arrs) {
            s[i] = Random.nextInt(objs)
            t[i] = Random.nextInt(objs - 1)
            if (t[i] == s[i]) {
                t[i] = objs - 1
            }
        }
    } else {
        for (i in 0 until arrs) {
            s[i] = Random.nextInt(objs)
            t[i] = Random.nextInt(objs)
        }
    }
    return FinGraph(objs, arrs, s, t)
}