package test.concurrent

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Created by CowardlyLion on 2020/2/1 22:00
 */
class BBB {
    var aaa = calc()
        private set

    private fun calc(): Int {
        println("aaa")
        return 6
    }

    fun aaaa() {
        runBlocking {
            launch { }

            withTimeoutOrNull(123){

            }

        }

    }

    fun foo(): Flow<Int> = flow {

    }

}