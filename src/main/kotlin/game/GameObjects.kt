package game

import game.main.Univ
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mu.toKLogger
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import java.lang.Exception

/**
 * Created by CowardlyLion on 2020/6/26 21:55
 */
class GameObjects(val univ: Univ) {

    //maybe SyncArray2 can extend to communicating clients
    val primitives = mutableListOf<Primitive>()

    val mutex = Mutex()

    suspend fun add(p: Primitive) {
        mutex.withLock {
            primitives += p
        }
    }
    suspend fun destroy(p: Primitive) {
        mutex.withLock {
            if (primitives.remove(p)) {
                p.destroy()
            }
        }
    }



    init {
        runBlocking {
            scan(arrayOf(
                "land.Oz",
                "land.Ev",
                "land.Norm",
                "game.debug"
            ))
        }
    }

    suspend fun scan(package_: Array<String>) {

        val reflections = Reflections(package_, univ.context.classLoader)
//        reflections.configuration.
//SubTypesScanner()
        val subtypes = reflections.getSubTypesOf<Primitive>(Primitive::class.java)
        mutex.withLock {

            subtypes.forEach {
                try {
                    val constructor = it.getConstructor(Univ::class.java)
                    val instance = constructor.newInstance(univ)
                    if (instance.instantiate) {
                        primitives += instance
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Univ.logger.info {
                        "instantiate \"${it.name}\" failed: ${e.toString()}"
                    }
                }

            }
        }

    }

}