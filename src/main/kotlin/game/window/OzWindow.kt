package game.window

import game.main.Univ
import glm_.vec2.Vec2i
import mu.KotlinLogging
import uno.glfw.GlfwMonitor
import uno.glfw.GlfwWindow
import uno.glfw.Key
import uno.glfw.glfw
import vkk.vk10.structs.Extent2D

/**
 * Created by CowardlyLion on 2020/4/20 12:37
 */
class OzWindow(
    val univ: Univ,
    width: Int, height: Int,
    title: String,
    monitor: GlfwMonitor? = null,
    position: Vec2i = Vec2i(Int.MIN_VALUE),
    installCallbacks: Boolean = true
) : GlfwWindow(width, height, title, monitor, position, installCallbacks) {

    val logger=KotlinLogging.logger {  }

    var resized = false
    get(){
        if (field) {
            resized=false
            return true
        }
        return false
    }

    override fun onWindowResized(newSize: Vec2i) {
        resized = true
    }

    override fun onKeyPressed(key: Key, mods: Int) {
        when (key) {
            Key.ESCAPE -> shouldClose = true
            else -> return
        }
    }
}