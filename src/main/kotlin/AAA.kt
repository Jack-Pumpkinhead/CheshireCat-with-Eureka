import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

/**
 * Created by CowardlyLion on 2020/1/13 20:19
 */
class AAA {
    fun aaa(){
        synchronized(this){

        }
        var bbb = BBB()
        val aaa = bbb.aaa
//        bbb.aaa = 6




    }

}

fun main() = runBlocking {



    withContext(Dispatchers.Default){

        counter++
    }
    println("end $counter")
    val job=launch {
        try {
            repeat(1000) {
                i ->
                println("job: aaa$i")
                delay(500)
            }
        } finally {
            withContext(NonCancellable) {
                println("not cancellable!")
                delay(1000)
                println("and can delay!")
            }
        }
    }

    delay(1400)
    println("aaaa")
    job.cancelAndJoin()
    println("aaaaaa")

    runBlocking {
        try {
            println("start")
            failed()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            delay(1000)
            println("True finally")
        }
    }
    return@runBlocking
}

suspend fun failed(): Int = coroutineScope {
    try {
        val one=async {
            try {
                delay(Long.MAX_VALUE)
                42
            } finally {
                println("First cancelled.")
            }
        }
        val two=async {

            async() {
                try {
                    println("s")
                    delay(10000)
                    println("ss")
                } finally {
                    println("Inner finally")
                }
            }
            delay(100)
            throw ArithmeticException("Exc")
        }
        return@coroutineScope one.await()
    } finally {
        println("failed finally")
    }

}

var counter = 0

suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100
    val k = 3
    val time = measureTimeMillis {
        coroutineScope {
            repeat(n) {
                launch{
                    repeat(k) {
                        action()
                    }
                }
            }
        }
    }

    println("aaa $time")

}