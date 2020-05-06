package game.main

import game.loop.FrameLoop
import game.loop.ticker.FixRateAcc
import game.main.OzConstants.HEIGHT
import game.main.OzConstants.TITLE
import game.main.OzConstants.WIDTH
import game.window.OzWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
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

    val logger=KotlinLogging.logger { }

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
    }

    val vulkan = OzVulkan(this, window)


    val scope = CoroutineScope(Dispatchers.Default)

    val ticker = FixRateAcc(initialDelayMillis = 100, context = scope.coroutineContext)

    val frameLoop = FrameLoop(this, window)

    fun start() {

        scope.launch {
            ticker.ticking()
        }


        frameLoop.loop()
    }

    fun destroy() {
        window.destroy()
        vulkan.destroy()
        glfw.terminate()
    }
}
