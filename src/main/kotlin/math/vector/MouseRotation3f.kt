package math.vector

import game.event.Events
import game.loop.TickableTS
import game.main.Univ
import game.window.OzWindow
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.gadget.Store
import math.gadget.TwoBool

/**
 * Created by CowardlyLion on 2020/6/28 11:14
 */
class MouseRotation3f(
    val rot: Vec3 = Vec3(),
    val step: Float = 0.005f,
    val events: Events,
    val window:OzWindow
):TickableTS{

    val tickS = Store(0L)

    val keyPress = TwoBool()

//    var locked = false
    var mouse = Vec2()


    suspend fun init() {
//        events.keyPress.subscribe {
//            if (it.key == Key.F) {
//                locked = !locked
//            }
//        }
    }

    val mutex = Mutex()

    override suspend fun update(tick: Long, timemillis: Long) {
        if (tickS.replace(tick)) {
            if (keyPress.turnTrue(window.cursorLocked)) {
                mouse = window.mousePos
            } else if (keyPress.remainTrue()) {
                val delta = window.mousePos - mouse
                /*Univ.logger.info {
                    "delta: $delta rot:$rot"
                }*/
                mouse = window.mousePos

                mutex.withLock {
                    rot.plusAssign(
                        Vec3(- delta.y.toFloat()* step, delta.x.toFloat() * step, 0)
                    )
                    //↓y   ->x (delta)(和screen coord一样)
                    rot.clampX(halfPI)
                }
            }
        }
    }


}