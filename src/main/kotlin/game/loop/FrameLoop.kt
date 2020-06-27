package game.loop

import game.event.FrameTick
import game.loop.TPSActor_old.Companion.getTPS
import game.loop.TPSActor_old.Companion.record
import game.main.Recorder2
import game.main.Univ
import game.window.OzWindow
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import mu.KotlinLogging
import org.springframework.beans.factory.getBean
import uno.glfw.glfw
import vkk.VkResult
import vkk.entities.VkFence
import vkk.extensions.acquireNextImageKHR
import vulkan.*
import vulkan.concurrent.SyncArray
import vulkan.drawing.PerImageConfiguration
import vulkan.drawing.Submit

class FrameLoop(val univ: Univ, val window: OzWindow) {

    val logger = KotlinLogging.logger { }

    val scope = CoroutineScope(Dispatchers.Default)

    val fps = TPSCounterC()


//    class Preparation(val univ: Univ, val image: Int)
//    val prepareChannel = BroadcastChannel<Preparation>()
// use actor + action collection

    lateinit var configurations: List<PerImageConfiguration>

    suspend fun makeConfigurations() {
        val ctx = univ.vulkan.swapchainContext
        val swapchain = ctx.getBean<OzSwapchain>()
        val commandPools = ctx.getBean<OzCommandPools>()
        val cb = commandPools.graphicCP.allocate().await()
        configurations = swapchain.images.mapIndexed { index, vkImage ->
            PerImageConfiguration(
                index, vkImage, swapchain.imageViews[index], swapchain.depth[index],
                ctx.getBean<OzFramebuffers>().fb_depth[index], ctx.getBean(), swapchain, ctx.getBean(), ctx.getBean()
            )
        }
    }


    val size = univ.vulkan.swapchain.images.size
    val semaphore = Semaphore(size)
    var semas = List(size) { univ.vulkan.device.semaphore() }

    init {
        runBlocking {
            univ.event.afterRecreateSwapchain.subscribe {
                makeConfigurations()
            }
            makeConfigurations()
        }
    }

    val submit = Submit(univ.vulkan)
//    val drawCmds = SyncArray<Recorder>()
    val drawCmds2 = SyncArray<Recorder2>()

    fun loop() {

        scope.launch {
            univ.event.perSecond.subscribe {
                val fps = fps.getTPS()
                logger.info {
                    "fps: $fps"
                }
            }
        }



        while (window.isOpen) {
            glfw.pollEvents()

            pauseForSize()


//            runBlocking {

            if (window.resized || univ.vulkan.shouldRecreate) {
                runBlocking {
                    univ.vulkan.recreateSwapchain(window.framebufferSize)
                }
                univ.vulkan.shouldRecreate = false

            }

//                semaphore.withPermit {  //暂时单线程

            val tick = runBlocking { fps.record() }
            runBlocking { univ.event.onFrameStart.send(FrameTick(tick, System.currentTimeMillis())) }


            val aquireS = semas[tick.rem(size).toInt()]


            var success = false

            val imageIndex = univ.vulkan.device.device.acquireNextImageKHR(
                swapchain = univ.vulkan.swapchain.swapchain,
                timeout = -1L,
                semaphore = aquireS,
                fence = VkFence.NULL,
                check = {
                    success = checkResult(it, "acquire")
                }
            )

            if (!success) {
                univ.vulkan.shouldRecreate = true

            }



            runBlocking {
                univ.vulkan.dms.update(imageIndex)  //update before record

                val cb = univ.vulkan.commandpools.graphicMutableCP.allocate().await()

                val fb = univ.vulkan.framebuffers.fb_depth[imageIndex]
                fb.begin(cb)
                drawCmds2.withLock {cmds->
                    cmds.forEach {
                        it(cb, univ.vulkan.dms.dmDescriptors[imageIndex])
                    }
                }
                fb.end(cb)


                val success = submit.submit(cb, aquireS, imageIndex)
                if (!success) {
                    univ.vulkan.shouldRecreate = true
                }
            }

//                    drawing.draw()
//                    val result = runBlocking { configurations[imageIndex].submit(aquireS)}


            runBlocking { univ.event.onFrameEnd.send(FrameTick(tick, System.currentTimeMillis())) }

//                }
//            }

            window.swapBuffers()

        }

    }

    fun checkResult(result: VkResult, state: String): Boolean {
        if (result == VkResult.ERROR_OUT_OF_DATE_KHR) {
            OzVulkan.logger.warn("$state: out of date")
            return false
        } else if (result != VkResult.SUCCESS && result != VkResult.SUBOPTIMAL_KHR) {
            OzVulkan.logger.warn("${result.description}")
            OzVulkan.logger.warn("$state: no success and no suboptimal")
            return false
        }
        return true
    }

    fun pauseForSize() {
        while (window.framebufferSize.allEqual(0)) {
            glfw.waitEvents()
        }
    }

//    val drawing = Drawing(univ.vulkan, this)


}