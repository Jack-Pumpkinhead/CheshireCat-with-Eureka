package game.loop

import game.main.Univ
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.sync.withLock

/**
 * Created by CowardlyLion on 2020/6/17 18:16
 */
class Gameloop(val univ: Univ) {


    val scope = CoroutineScope(Dispatchers.Default)

    val TPS = 100
    val dt = 1.0 / TPS

    val ticker = ticker(1000 / TPS.toLong(), 30)


    val tps = TPSCounterC()

    var running = true
    fun stop() {
        running = false
    }
    suspend fun waitStop() {
        running = false
        complete.join()

    }


    val complete = Job()
    val initialized = Job()

//    val task = SyncArray<TickTimeAction>()


    suspend fun loop() {

        univ.loader.loading()

        univ.gameObjects.apply {
            mutex.withLock {
                primitives.forEach {
                    it.initialize()
                }
            }
        }

        initialized.complete()

        while (running) {
            ticker.receive()

            val tick = tps.counter
            val timemillis = System.currentTimeMillis()
            univ.matrices.fpv.update(tick, timemillis)

            univ.gameObjects.apply {
                mutex.withLock {
                    primitives.forEach {
                        if (it.active) {
                            it.gameloop(tick, timemillis)
                        }
                    }

                }
            }

            /* task.withLockS {
                 it.forEach {
                     it(tick, timemillis)
                 }
             }
 */

            tps.record()
        }

        univ.gameObjects.apply {
            mutex.withLock {
                primitives.forEach {
                    it.destroy()
                }

            }
        }

        complete.join()

    }

}