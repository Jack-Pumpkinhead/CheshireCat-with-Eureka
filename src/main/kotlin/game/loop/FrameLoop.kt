package game.loop

import game.loop.TPSActor.Companion.getTPS
import game.loop.TPSActor.Companion.getTotal
import game.loop.TPSActor.Companion.record
import game.main.Univ
import game.window.OzWindow
import kotlinx.coroutines.*
import mu.KotlinLogging
import uno.glfw.glfw
import vulkan.drawing.DrawFrame

class FrameLoop(val univ: Univ, val window: OzWindow) {

    val logger = KotlinLogging.logger { }

    val listeners = mutableListOf<Tickable>()

//    val tick = TPSCounter()

    val scope = CoroutineScope(Dispatchers.Default)

    val fps = TPSActor.launch(scope)

    val tick = univ.ticker.subscribe()

    fun loop() {

        scope.launch {
            printFPS()
        }

        while (window.isOpen) {
            glfw.pollEvents()

            pauseForSize()

            if (window.resized) {
                univ.vulkan.recreateRenderpass(window.framebufferSize)
            }

            var tick = 0L
            runBlocking { tick = fps.getTotal()}


            listeners.forEach { it.update(tick) }
            drawframe.draw()


            runBlocking { fps.record() }
            window.swapBuffers()
        }




    }

    fun pauseForSize() {
        while (window.framebufferSize.allEqual(0)) {
            glfw.waitEvents()
        }
    }

    val drawframe = DrawFrame(univ.vulkan, univ.vulkan.device, this)

    suspend fun printFPS() {
        while (window.isOpen) {
            tick.receive()

            val fps = fps.getTPS()
            logger.info {
                "fps: $fps"
            }
        }
    }

}