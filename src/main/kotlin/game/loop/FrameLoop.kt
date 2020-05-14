package game.loop

import game.loop.TPSActor.Companion.getTPS
import game.loop.TPSActor.Companion.getTotal
import game.loop.TPSActor.Companion.record
import game.main.Univ
import game.window.OzWindow
import kotlinx.coroutines.*
import mu.KotlinLogging
import uno.glfw.glfw
import vulkan.drawing.Drawing

class FrameLoop(val univ: Univ, val window: OzWindow) {

    val logger = KotlinLogging.logger { }

    val scope = CoroutineScope(Dispatchers.Default)

    val fps = TPSActor.launch(scope)

    val tick = univ.ticker.subscribe()


//    class Preparation(val univ: Univ, val image: Int)
//    val prepareChannel = BroadcastChannel<Preparation>()
// use actor + action collection

    fun loop() {

        val job = scope.launch {
            while (isActive) {
                printFPS()
            }
        }


        while (window.isOpen) {
            glfw.pollEvents()

            pauseForSize()




            if (window.resized || univ.vulkan.shouldRecreate) {
                runBlocking {
                    univ.vulkan.recreateSwapchain(window.framebufferSize)
                }
            }

            val tick = runBlocking { fps.getTotal() }


//            listeners.forEach { it.update(tick) }
//            drawframe.draw()


            drawing.draw()


            runBlocking { fps.record() }
            window.swapBuffers()
        }

        job.cancel()

    }

    fun pauseForSize() {
        while (window.framebufferSize.allEqual(0)) {
            glfw.waitEvents()
        }
    }

    val drawing = Drawing(univ.vulkan, this)


    //use while(window.isActive) would throw exception at close
    suspend fun printFPS() {
        tick.receive()

        val fps = fps.getTPS()
        logger.info {
            "fps: $fps"
        }
        /*logger.info {
            "window framebuffer size: ${univ.window.framebufferSize}"
        }
        logger.info {
            "surface size: ${univ.vulkan.physicalDevice.pd.getSurfaceCapabilitiesKHR(univ.vulkan.surface.surface).currentExtent.size}"
        }*/
        /*logger.info {
            "cmds: ${univ.vulkan.framebuffer.fbs[0].drawCmds.size}"
        }*/
    }

}