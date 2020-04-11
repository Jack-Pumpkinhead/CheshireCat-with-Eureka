package concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicInteger
import kotlin.system.measureTimeMillis

/**
 * Created by CowardlyLion on 2020/4/8 14:06
 */

//@Volatile
//var counter = AtomicInteger()
var counter = 0

//val counterContext = newSingleThreadContext("CounterContext")
val mutex = Mutex()

fun main() = runBlocking {
    withContext(Dispatchers.Default){
        massiveRun {
            mutex.withLock {
                counter++
            }
        }
    }
    println("Counter = $counter")
}

suspend fun massiveRun(action: suspend () -> Unit) {
    val n = 100
    val k = 1000
    val time= measureTimeMillis {
        coroutineScope {
            repeat(n) {
                launch {
                    repeat(k) { action()}
                }
            }
        }
    }
    println("Completed ${n * k} actions in $time ms")
}