package game.input

import game.window.OzWindow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import uno.glfw.Key
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by CowardlyLion on 2020/6/30 22:22
 */
class KeyMap(val window: OzWindow) {

    val pressed = ConcurrentHashMap<Key, Boolean>()

    fun isPressed(key: Key): Boolean {
        return pressed.computeIfAbsent(key) {
            window.isPressed(key)
        }
    }

  /*  val pressed = mutableMapOf<Key, Boolean>()

    val mutex = Mutex()

    suspend fun isPressed(key: Key): Boolean {
        return mutex.withLock {
            pressed.computeIfAbsent(key) {
                window.isPressed(key)
            }
        }
    }
*/
}