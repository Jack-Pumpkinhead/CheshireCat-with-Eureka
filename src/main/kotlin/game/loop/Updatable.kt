package game.loop

import org.lwjgl.system.MemoryStack

interface Updatable {
    fun update()
}

interface Tickable {
    fun update(tick: Long)
}
interface TickableT {
    fun update(tick: Long, timemillis: Long)
}
interface TickableTS {
    suspend fun update(tick: Long, timemillis: Long)
}


interface Listener {
    fun update(msg: String)
}