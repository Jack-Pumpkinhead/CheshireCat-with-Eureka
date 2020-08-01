package math.matrix

import game.event.Events
import game.main.Univ
import game.window.OzWindow
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2i

/**
 * Created by CowardlyLion on 2020/6/29 16:18
 */
class ProjectionOrthogonal(val window: OzWindow, val events: Events) {

    var left = -10F
    var right = 10F
    var bottom = -10F
    var top = 10F
    var zNear = 0F
    var zFar = 100F

//    val mat = glm.ortho(-10F, 10F, -10F, 10F, 0F, 1F)
//    val mat = glm.orthoLhZo(-10F, 10F, -10F, 10F, 0F, 100F)

    fun assign(mat: Mat4) {
//        glm.orthoLhZo(-10F, 10F, -10F, 10F, 0F, 100F, mat)
        glm.orthoLhZo(left, right, bottom, top, zNear, zFar, mat)
    }

    suspend fun init() {
        events.windowResize.subscribe {
            size = it.size
        }
        events.mouseScroll.subscribe {
            magnify += it.delta
//            Univ.logger.info {
//                "mag: $magnify"
//            }
        }

    }

    var magnify = 10F
    var size = window.framebufferSize
    suspend fun update() {
        val halfX = size.x / 2
        val halfY = size.y / 2
        left = -halfX / magnify
        right = halfX / magnify
        bottom = -halfY / magnify
        top = halfY / magnify

    }

}