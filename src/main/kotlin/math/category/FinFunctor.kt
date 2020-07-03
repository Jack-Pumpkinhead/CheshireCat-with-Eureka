package math.category

/**
 * Created by CowardlyLion on 2020/7/3 20:30
 */
class FinFunctor(
    val S:FinCategory,
    val T:FinCategory,
    val objF:IntArray,
    val arrF:IntArray
) {

    fun F0(a: Int) = objF[a]
    fun F1(f: Int) = arrF[f]


    fun isFunctor():Boolean{
        for (i in 0 until S.arrows) {
            if (T.source(F1(i)) != F0(S.source(i)) || T.target(F1(i)) != F0(S.target(i))) {
                return false
            }
        }
        for (g in 0 until S.arrows) {
            for (f in 0 until S.arrows) {
                if (S.composible(g, f)) {
                    if (F1(S.comp(g, f)) != T.comp(F1(g), F1(f))) {
                        return false
                    }
                }
            }
        }
        return true
    }


}