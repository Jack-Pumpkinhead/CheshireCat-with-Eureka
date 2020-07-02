package math.matrix

import game.Primitive
import game.event.Events
import game.loop.Gameloop
import game.main.Univ
import game.window.OzWindow
import kotlinx.coroutines.runBlocking

/**
 * Created by CowardlyLion on 2020/6/29 12:07
 */
class Matrices(val events: Events,val window:OzWindow,val gameloop: Gameloop) {

    val projOrthogonal = ProjectionOrthogonal()
    val projPerspective = ProjectionPerspective(window,events)

    val fpv = FirstPersonView(events = events, window = window, dt = gameloop.dt.toFloat())

    init {

        runBlocking {
            projPerspective.init()
            fpv.init()
        }
    }

}