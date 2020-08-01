package math.matrix

import game.Primitive
import game.event.Events
import game.loop.Gameloop
import game.main.Univ
import game.window.OzWindow
import glm_.vec3.Vec3
import kotlinx.coroutines.runBlocking

/**
 * Created by CowardlyLion on 2020/6/29 12:07
 */
class Matrices(val events: Events,val window:OzWindow,val gameloop: Gameloop) {

    val projOrthogonal = ProjectionOrthogonal(window, events)
    val projPerspective = ProjectionPerspective(window, events)

    val fpv = FirstPersonView(
        p= Vec3(-2.86,-3.30,-7.94),
        rot = Vec3(-0.07,0.30,0.00),
        events = events,
        window = window,
        dt = gameloop.dt.toFloat()
    )

    init {

        runBlocking {
            projOrthogonal.init()
            projPerspective.init()
            fpv.init()
        }
    }

}