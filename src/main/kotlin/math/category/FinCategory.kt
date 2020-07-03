package math.category

/**
 * Created by CowardlyLion on 2020/7/3 18:50
 */
class FinCategory(
    val objs: Int,
    val arrows: Int,    // [-objs, -1] is id
    val s: IntArray,
    val t: IntArray,
    val comp: List<IntArray>
) {
    init {
        assert(s.size==arrows)
        assert(t.size==arrows)
    }

    fun id(a: Int): Int = -a - 1

    fun comp(g: Int, f: Int): Int {
        return when {
            g < 0 -> f
            f < 0 -> g
            else -> comp[g][f]
        }
    }
    fun source(f:Int): Int {
        return if (f < 0) -f - 1 else s[f]
    }
    fun target(f:Int) = t[f]
    fun composible(g: Int, f: Int):Boolean {
        return source(g) == target(f)
    }

    fun isCategory() = isComp() && isAssociative()

    fun isComp(): Boolean {
        for (g in 0 until arrows) {
            for (f in 0 until arrows) {
                if (composible(g, f)) {
                    val h = comp(g, f)
                    if (source(h) != source(f) || target(h) != target(g)) {
                        return false
                    }
                }
            }
        }
        return true
    }

    fun isAssociative(): Boolean {
        for (h in 0 until arrows) {
            for (g in 0 until arrows) {
                for (f in 0 until arrows) {
                    if (composible(h, g) && composible(g, f)) {
                        val a = comp(comp(h, g), f)
                        val b = comp(h, comp(g, f))
                        if (a != b) {
                            return false
                        }
                    }
                }
            }
        }
        return true
    }


}