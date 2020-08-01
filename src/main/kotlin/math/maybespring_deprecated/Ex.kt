package math.maybespring_deprecated


/**
 * Created by CowardlyLion on 2020/5/7 15:10
 */
abstract class Ex<T>(
    val innerName: String,
    val version: Long,
    val obj: T
) {

    fun destroy() {
    }


}