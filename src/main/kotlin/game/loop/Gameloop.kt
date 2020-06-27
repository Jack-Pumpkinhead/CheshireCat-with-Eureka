package game.loop

import game.Primitive
import game.loop.TPSActor_old.Companion.record
import game.main.Univ
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.sync.withLock
import vulkan.drawing.TestPBasic
import kotlin.random.Random

/**
 * Created by CowardlyLion on 2020/6/17 18:16
 */
class Gameloop(val univ: Univ) {


    val scope = CoroutineScope(Dispatchers.Default)
    val ticker = ticker(1000/100,0)


    val tps = TPSCounterC()

    var running = true





    suspend fun loop() {
        univ.gameObjects.apply {
            mutex.withLock {
                primitives.forEach{
                    it.initialize()
                }
            }
        }

        while (running) {
            ticker.receive()

            univ.gameObjects.apply {
                mutex.withLock {
                    primitives.forEach {
                        it.gameloop(tps.counter, System.currentTimeMillis())
                    }

                }
            }


            tps.record()
        }

        univ.gameObjects.apply {
            mutex.withLock {
                primitives.forEach {
                    it.destroy()
                }

            }
        }

    }

}