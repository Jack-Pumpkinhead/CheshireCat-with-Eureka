package test.concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.system.measureTimeMillis

/**
 * Created by CowardlyLion on 2020/4/8 14:06
 */

//@Volatile
//var test.getCounter = AtomicInteger()
var counter2 = 0

//val counterContext = newSingleThreadContext("CounterContext")
val mutex = Mutex()

fun mian2() = runBlocking {
    withContext(Dispatchers.Default){
        massiveRun1 {
            mutex.withLock {
                counter2++
            }
        }
    }
    println("Counter = $counter2")
}

suspend fun massiveRun1(action: suspend () -> Unit) {
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