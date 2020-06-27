package game.loop

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by CowardlyLion on 2020/6/27 22:54
 */
class TPSCounterC { //concurrent version
    val timeStamp = mutableListOf<Long>()
    var counter: Long = 0
        private set

    val mutex = Mutex()


    suspend fun record(): Long = mutex.withLock {
        timeStamp.add(System.currentTimeMillis())
        ++counter
    }

    suspend fun getTPS() = mutex.withLock {
        forget(1000)
        timeStamp.size
    }

    private fun forget(age: Long) {
        val time = System.currentTimeMillis() - age
        timeStamp.removeAll { it < time }
    }

}