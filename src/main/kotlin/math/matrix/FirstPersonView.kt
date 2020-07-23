package math.matrix

import game.event.Events
import game.loop.TickableTS
import game.window.OzWindow
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.withLock
import physics.DragForce
import math.vector.MouseRotation3f
import physics.SingleParticle
import uno.glfw.Key

/**
 * Created by CowardlyLion on 2020/6/28 21:03
 */
class FirstPersonView(
    p: Vec3 = Vec3(),
    v: Vec3 = Vec3(),
    rot: Vec3 = Vec3(),
    dt:Float,
    val window:OzWindow,
    val events: Events
):TickableTS {

    val pos: SingleParticle = SingleParticle(p, v, dt = dt)
    val mouseRotation = MouseRotation3f(rot, events = events, window = window)

    val drag =
        DragForce({ 1F }, { Vec3() }, { if (window.pressed(Key.LEFT_SHIFT)) 7F else 3F })

    init {
//        runBlocking {
//            init()
//        }
    }

   suspend fun init() {
       pos.mutex.withLock {

           pos.forces += {
               drag.get(pos.p, pos.v)
           }
           pos.forces += {
               val disp = Vec3(
                   加减(Key.D,Key.A), //右
                   加减(Key.Q,Key.E), //下
                   加减(Key.W,Key.S)  //前
               )
//           Univ.logger.info {
//               "disp: $disp"
//           }
               View.rotate(mouseRotation.rot,disp).times(force)
//               val vec = glm.rotateZ(Vec3(),disp,mouseRotation.rot.z)
//               glm.rotateX(vec,vec,mouseRotation.rot.x)
//               glm.rotateY(vec,vec,mouseRotation.rot.y)
//               vec.times(force)
           }
       }

       events.mouseScroll.subscribe {
           force += it.delta * 10
       }

   }

//    val mat get() = View.view(pos.p, mouseRotation.rotation)
    suspend fun getMatrix():Mat4 {
        return pos.mutex.withLock {
            mouseRotation.mutex.withLock {
                View.view(pos.p, mouseRotation.rot)
            }
        }
    }
    suspend fun assign(mat4: Mat4) {
        pos.mutex.withLock {
            mouseRotation.mutex.withLock {
                View.view(pos.p, mouseRotation.rot, mat4)
            }
        }
    }



    override suspend fun update(tick: Long, timemillis: Long) {
        pos.update(tick, timemillis)
        mouseRotation.update(tick, timemillis)
    }

    var force = 47F

    inline fun 加减(a: Boolean, b: Boolean) = (if (a) 1 else 0) - (if (b) 1 else 0)
    inline fun 加减(a: Key, b: Key) = 加减(window.pressed(a), window.pressed(b))


    fun forward(distance: Float): Vec3 {
        val viewVector = View.viewVector(mouseRotation.rot)
        viewVector.timesAssign(distance)
        return pos.p.plus(viewVector)
    }
    fun direction(): Vec3 {
        return View.viewVector(mouseRotation.rot)
    }


    }