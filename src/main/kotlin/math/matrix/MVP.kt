package math.matrix

import game.loop.TickableTS

/**
 * Created by CowardlyLion on 2020/6/29 12:01
 */
class MVP(val proj: ProjectionPerspective, val view: FirstPersonView, val model: Model):TickableTS {

    override suspend fun update(tick: Long, timemillis: Long) {
        view.update(tick, timemillis)
    }




}