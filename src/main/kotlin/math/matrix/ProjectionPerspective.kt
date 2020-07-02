package math.matrix

import game.event.Events
import game.window.OzWindow
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.tCos
import math.tSin
import vulkan.OzVulkan
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * Created by CowardlyLion on 2020/6/29 16:58
 */
class ProjectionPerspective(val window: OzWindow, val events: Events) {

    val fov = Math.toRadians(60.0).toFloat()
    val a = tan(fov / 2)    //60 degree 当 radians ？ tan90°
    val near = 0.1F
    val far = 100F

//    var mat = glm.perspectiveFov(60F, window.framebufferSize, 0.1F, 100F)
    var mat = calc(window.framebufferSize)

    val mutex = Mutex()

    init {
//        runBlocking {
//            init()
//        }
    }

    suspend fun init() {
        events.windowResize.subscribe {
            update(it.size)
        }
    }



    fun calc(size: Vec2i): Mat4 {
        return glm.perspectiveFovLhZo(fov, size, near, far)
    }

//        return Mat4(
//            (1 / a) * size.y/size.x, 0, 0, 0,
//            0, 1 / a, 0, 0,
//            0, 0, -far / (far - near), -far * near / (far - near),
//            0, 0, -1, 0
//        ).transposeAssign()
//        )
//    }

//    )
//    fun calc(size: Vec2i) = Mat4(
//        1 / (a * tCos(size.x.toFloat(), size.y.toFloat())), 0, 0, 0,
//        0, 1 / (a * tSin(size.x.toFloat(), size.y.toFloat())), 0, 0,
//        0, 0,far / (far - near),- far * near / (far - near),
//        0, 0, 1, 0
//    ).transposeAssign().times(Mat4(-1),Mat4())
//    ).transposeAssign()
//    )



    suspend fun update(size: Vec2i) {
        mat = calc(size)
//        mat = Mat4()
//        mutex.withLock {
//            glm.perspectiveFovRh(100F, size, 0.01F, 10F, mat)
//        }
    }

    suspend fun copy(): Mat4 {
        return mat
//        return mutex.withLock {
//            Mat4(mat)
//        }
    }

}