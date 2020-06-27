package vulkan.concurrent

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by CowardlyLion on 2020/6/8 17:25
 */
class SyncArray<T> {

    val mutex = Mutex()
    private val array = mutableListOf<T>()

    suspend fun withLock(action: (MutableList<T>) -> Unit) {
        mutex.withLock {
            action(array)
        }
    }
    suspend fun withLockS(action: suspend (MutableList<T>) -> Unit) {
        mutex.withLock {
            action(array)
        }
    }

}