package math.gadget

/**
 * Created by CowardlyLion on 2020/6/28 11:17
 */
class TwoBool(var a: Boolean = false, var b: Boolean = false) {

    fun insert(b_: Boolean) {
        a = b
        b = b_
    }

    fun matches(a_: Boolean, b_: Boolean) = (a == a_ && b == b_)

    fun turnTrue(b_: Boolean): Boolean {
        insert(b_)
        return !a && b
    }

    fun remainTrue() = a && b

}