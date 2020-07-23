package game.main

import game.GameObjects
import game.debug.DebugJFrame
import game.entity.Emeralds
import game.event.Events
import game.input.GLSLoader
import game.input.SpringInput
import game.loop.FrameLoop
import game.loop.Gameloop
import game.buildin.Buildin
import game.main.OzConstants.HEIGHT
import game.main.OzConstants.TITLE
import game.main.OzConstants.WIDTH
import game.window.OzWindow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.TickerMode
import kotlinx.coroutines.channels.ticker
import math.matrix.Matrices
import mu.KotlinLogging
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import uno.glfw.glfw
import uno.glfw.windowHint
import vulkan.OzVulkan
import vulkan.drawing.OzObjects

/**
 * Created by CowardlyLion on 2020/4/20 12:51
 */

fun main() {
    val univ = Univ()
    univ.start()
    univ.destroy()
}

class Univ(){

    companion object {
        val logger = KotlinLogging.logger { }
    }

    val context = GenericApplicationContext()
    val springInput: SpringInput
    val glsl:GLSLoader
    val emeralds:Emeralds
    val events: Events
    val objects: OzObjects



    val window: OzWindow
    init {
        glfw.init()
        glfw.windowHint {
            api = windowHint.Api.None
            resizable = true
        }
        window = OzWindow(
            univ = this,
            width = WIDTH,
            height = HEIGHT,
            title = TITLE,
            installCallbacks = true
        )
        window.installDefaultCallbacks()

        val beans = beans {
            bean<OzWindow> { window }
            bean<SpringInput>()
            bean<GLSLoader>(destroyMethodName = "destroy")
            bean<Emeralds>()
            bean<Events>()
            bean<OzObjects>()
        }
        beans.initialize(context)
        context.refresh()
        springInput = context.getBean()
        glsl = context.getBean()
        emeralds = context.getBean()
        events = context.getBean()
        objects = context.getBean()


//        glsl.init()
    }

    val vulkan = OzVulkan(this, window)


    val scope = CoroutineScope(Dispatchers.Default)

    val frameLoop = FrameLoop(this, window)
    val gameloop = Gameloop(this)

    val loader = Buildin(this)
    val matrices = Matrices(events,window, gameloop)


    //create late
    val gameObjects:GameObjects = GameObjects(this)


    val debug: DebugJFrame = DebugJFrame(this)

    fun start() {

        events.launch(scope)

        scope.launch {
            val ticker = ticker(1000, 0, this.coroutineContext, TickerMode.FIXED_PERIOD)
            var i = 0L
            while (isActive) {
                ticker.receive()
                events.perSecond.send(i)
                i++
            }
        }




        scope.launch {
            gameloop.loop()
        }

        runBlocking {
            gameloop.initialized.join()
            logger.info {
                "game primitives initialized"
            }
        }

        frameLoop.loop()
        gameloop.stop()
    }

    fun destroy() {
        window.destroy()
        vulkan.destroy()
        glfw.terminate()
    }
}
