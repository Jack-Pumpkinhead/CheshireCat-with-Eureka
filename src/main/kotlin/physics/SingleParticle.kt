package physics

import game.loop.TickableT
import game.loop.TickableTS
import game.main.Force
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by CowardlyLion on 2020/6/28 19:05
 */
class SingleParticle(
    var p: Vec3 = Vec3(),
    var v: Vec3 = Vec3(),
    var a: Vec3 = Vec3(),
    var f: Vec3 = Vec3(),
    var m: Float = 1F,
    var dt: Float = 0.1F,
    val forces: MutableList<Force> = mutableListOf()
): TickableTS {

    val mutex = Mutex()

    override suspend fun update(tick: Long, timemillis: Long) {
        mutex.withLock {
            f.put(0, 0, 0)
            for (force in forces) {
                f.plusAssign(force(this))
            }
            f.div(m, a)
            v.plusAssign(a.times(dt))
            p.plusAssign(v.times(dt))
        }
    }



}