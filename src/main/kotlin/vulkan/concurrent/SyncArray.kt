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
    suspend fun <R> withLockR(action: (MutableList<T>) -> R):R {
        return mutex.withLock {
            action(array)
        }
    }


    suspend fun withLockS(action: suspend (MutableList<T>) -> Unit) {
        mutex.withLock {
            action(array)
        }
    }
    suspend fun <R> withLockRS(action: suspend (MutableList<T>) -> R):R {
        return mutex.withLock {
            action(array)
        }
    }


    private val recycle = mutableListOf<Int>()

    suspend fun assign(obj: T): Int {
        mutex.withLock {
            if (recycle.isNotEmpty()) {
                val i = recycle.removeAt(recycle.lastIndex)
                array[i] = obj
                return i
            } else {
                array += obj
                return array.size - 1
            }
        }
    }

    suspend fun recycle(i: Int) {
        mutex.withLock {
            recycle += i
        }
    }


}