package game.main

import game.GameObjects
import game.entity.Emeralds
import game.event.Events
import game.input.GLSLoader
import game.input.SpringInput
import game.loop.FrameLoop
import game.loop.Gameloop
import game.main.OzConstants.HEIGHT
import game.main.OzConstants.TITLE
import game.main.OzConstants.WIDTH
import game.window.OzWindow
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.TickerMode
import kotlinx.coroutines.channels.ticker
import mu.KotlinLogging
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import uno.glfw.glfw
import uno.glfw.windowHint
import vulkan.OzVulkan

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
    val gameObjects:GameObjects


    val event = Events()

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
            bean<SpringInput>()
            bean<GLSLoader>(destroyMethodName = "destroy")
            bean<Emeralds>()
        }
        beans.initialize(context)
        context.refresh()
        springInput = context.getBean()
        glsl = context.getBean()
        emeralds = context.getBean()
        gameObjects = GameObjects(this)

//        glsl.init()
    }

    val vulkan = OzVulkan(this, window)


    val scope = CoroutineScope(Dispatchers.Default)

    val frameLoop = FrameLoop(this, window)

    val gameloop = Gameloop(this)

    fun start() {

        runBlocking {
            event.launch(scope)
        }

        scope.launch {
            val ticker = ticker(1000, 0, this.coroutineContext, TickerMode.FIXED_PERIOD)
            var i = 0L
            while (isActive) {
                ticker.receive()
                event.perSecond.send(i)
                i++
            }
        }




        scope.launch {
            gameloop.loop()
        }


        frameLoop.loop()
    }

    fun destroy() {
        window.destroy()
        vulkan.destroy()
        glfw.terminate()
    }
}
