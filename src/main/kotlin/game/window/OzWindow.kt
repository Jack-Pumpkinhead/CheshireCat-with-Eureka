package game.window

import game.event.*
import game.main.Univ
import glm_.vec2.Vec2
import glm_.vec2.Vec2i
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import uno.glfw.*
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by CowardlyLion on 2020/4/20 12:37
 */
class OzWindow(
    val univ: Univ,
    width: Int, height: Int,
    title: String,
    monitor: GlfwMonitor? = null,
    position: Vec2i = Vec2i(Int.MIN_VALUE),
    installCallbacks: Boolean
) : GlfwWindow(width, height, title, monitor, position, installCallbacks) {


    companion object {
        val logger = KotlinLogging.logger { }
    }

    var resized = false
    get(){
        if (field) {
            resized=false
            return true
        }
        return false
    }

    fun lockMouse() {
        cursorMode = CursorMode.disabled
    }
    fun unlockMouse() {
        cursorMode = CursorMode.normal

    }
    var cursorLocked = false
//    fun cursorLocked(): Boolean = cursorMode == CursorMode.disabled

    var windowSize = size

    override fun onWindowResized(newSize: Vec2i) {
        windowSize = newSize
        resized = true
        runBlocking {
            univ.events.windowResize.send(WindowResize(newSize))
        }
    }

//    private val keyMap = KeyMap(this)
//    fun pressed(key: Key): Boolean {
//        return keyMap.isPressed(key)
//    }

    val pressed = ConcurrentHashMap<Key, Boolean>()

    fun pressed(key: Key): Boolean {
        return pressed.computeIfAbsent(key) {
            isPressed(key)
        }
    }

    override fun onKeyPressed(key: Key, mods: Int) {

        pressed[key] = true

        runBlocking {
            univ.events.keyPress.send(KeyPress(key, mods))
        }
        when (key) {
            Key.ESCAPE -> {
                shouldClose = true
                univ.gameloop.stop()
//                runBlocking {
//                    univ.gameloop.waitStop()
//                }
            }
            Key.F -> {
                if (cursorMode == CursorMode.normal) {
                    cursorMode = CursorMode.disabled
                    cursorLocked = true
                } else {
                    cursorMode = CursorMode.normal
                    cursorLocked = false
                }
            }

            else -> return
        }
    }

    override fun onKeyReleased(key: Key, mods: Int) {

        pressed[key] = false

        runBlocking {
            univ.events.keyRelease.send(KeyRelease(key, mods))
        }
    }

    var mousePos = Vec2(cursorPos)

    override fun onMouseEntered() {
        runBlocking {
            univ.events.mouseEnter.send(MouseEnter())
        }
    }

    override fun onMouseExited() {
        runBlocking {
            univ.events.mouseExit.send(MouseExit())
        }
    }

    override fun onMouseMoved(newPos: Vec2) {
        mousePos = newPos
        runBlocking {
            univ.events.mouseMove.send(MouseMove(newPos))
        }
    }

    override fun onMousePressed(button: MouseButton, mods: Int) {
        runBlocking {
            univ.events.mousePress.send(MousePress(button, mods))
        }
    }

    override fun onMouseReleased(button: MouseButton, mods: Int) {
        runBlocking {
            univ.events.mouseRelease.send(MouseRelease(button, mods))
        }
    }

    var scroll = 0F

    override fun onMouseScrolled(delta: Float) {
        scroll += delta
        runBlocking {
//            logger.info {
//                "MouseScrolled: $delta"
//            }
            univ.events.mouseScroll.send(MouseScroll(delta))
        }
    }

}