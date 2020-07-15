package math.graph

/**
 * Created by CowardlyLion on 2020/7/6 18:31
 */
class FinGraph(val objs: Int,
               val arrs: Int,
               val s: IntArray,
               val t: IntArray) {

    fun from(a: Int): IntArray = s.indices.filter { s[it] == a }.toIntArray()
    fun to(a: Int): IntArray = t.indices.filter { t[it] == a }.toIntArray()
    fun hom(a: Int, b: Int) = (0 until arrs).filter { s[it] == a && t[it] == b }.toIntArray()

    fun source(f:Int): Int = s[f]
    fun target(f:Int): Int = t[f]
}