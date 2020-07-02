package math.gadget

/**
 * Created by CowardlyLion on 2020/6/28 10:58
 */
class Store<T>(var obj: T) {
    fun replace(a: T): Boolean =
        if (obj != a) {
            obj = a
            true
        } else false

}