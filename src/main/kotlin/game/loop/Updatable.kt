package game.loop

import org.lwjgl.system.MemoryStack

interface Updatable {
    fun update()
}

interface Tickable {
    fun update(tick: Long)
}

interface Listener {
    fun update(msg: String)
}