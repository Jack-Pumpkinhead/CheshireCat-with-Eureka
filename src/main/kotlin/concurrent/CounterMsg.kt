package concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor

/**
 * Created by CowardlyLion on 2020/4/8 14:55
 */

fun main() = runBlocking<Unit> {
    val counter = counterActor()
    withContext(Dispatchers.Default) {
        massiveRun {
            counter.send(IncCounter)
        }
    }

    val responce = CompletableDeferred<Int>()
    counter.send(GetCounter(responce))
    println("Counter = ${responce.await()}")
    counter.close()
}

sealed class CounterMsg
object IncCounter:CounterMsg()
class GetCounter(val responce: CompletableDeferred<Int>) : CounterMsg()

fun CoroutineScope.counterActor() = actor<CounterMsg>{
    var counter = 0
    for (msg in channel) {
        when (msg) {
            is IncCounter -> counter++
            is GetCounter -> msg.responce.complete(counter)
        }
    }
}
