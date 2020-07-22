package vulkan.concurrent

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by CowardlyLion on 2020/7/20 22:02
 */
class SyncArray2<T>  {

    //destroyed: Boolean
    inner class InArr(
        var obj: T,
        var index: Int,
        var active: Boolean = true,
        var markDestroyed: Boolean = false,
        var changed: Boolean = true
    ){
        suspend fun replace(newObj: T) {
            this@SyncArray2.mutex.withLock {
                obj = newObj
            }
        }
        suspend fun <R> withLock(action: (T) -> R): R {
            return this@SyncArray2.mutex.withLock {
                action(obj)
            }
        }
        suspend fun <R> withLockS(action: suspend (T) -> R): R {
            return this@SyncArray2.mutex.withLock {
                action(obj)
            }
        }


        suspend fun replace(map: (T) -> T) {
            val newObj = map(obj)
            this@SyncArray2.mutex.withLock {
                obj = newObj
            }
        }

        fun deactivate() {
            active = false
        }
        fun markDestroyed() {
            markDestroyed = true
        }
        fun markChanged() {
            changed = true
        }


}


    val mutex = Mutex()
    private val array = mutableListOf<InArr>()


    suspend fun <R> withLock(action: (MutableList<InArr>) -> R):R {
        return mutex.withLock {
            action(array)
        }
    }


    suspend fun <R> withLockS(action: suspend (MutableList<InArr>) -> R) {
        mutex.withLock {
            action(array)
        }
    }

    suspend fun assign(obj: T): InArr {
        return mutex.withLock {
            val obj_ = InArr(obj, array.size)
            array += obj_
            obj_
        }
    }


    suspend fun forEachActive_ifHas(
        preAction: suspend () -> Unit,
        action: suspend (T) -> Unit,
        postAction: suspend () -> Unit
    ) {
        mutex.withLock {
            val actives = array.filter {
                it.active && !it.markDestroyed
            }
            if (actives.isNotEmpty()) {
                preAction()
                actives.forEach {
                    action(it.obj)
                }
                postAction()
            }
        }
    }

    suspend fun forEachActive(action: (T) -> Unit) {
        mutex.withLock {
            array.forEach {
                if (it.active && !it.markDestroyed) {
                    action(it.obj)
                }
            }
        }
    }


    suspend fun clearMarkDestroyed() {
        mutex.withLock {
            var i = 0
            while (i < array.size) {
                if (array[i].markDestroyed) {
                    val obj = array.removeAt(i)
                    obj.index = -1
                    continue
                } else {
                    array[i].index = i
                    i++
                }
            }
        }
    }


}