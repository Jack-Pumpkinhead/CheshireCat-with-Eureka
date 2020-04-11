/**
 * Created by CowardlyLion on 2020/4/8 14:47
 */
sealed class SealedTest {
    data class C(val c: (Int)->SealedTest):SealedTest()
    data class CC(val cc: (SealedTest)->(SealedTest)):SealedTest()

}