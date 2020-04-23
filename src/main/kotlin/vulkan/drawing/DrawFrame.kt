package vulkan.drawing

import game.loop.FrameLoop
import game.loop.TPSActor.Companion.getTotal
import kotlinx.coroutines.runBlocking
import land.oz.turorial.HelloTriangle
import mu.KotlinLogging
import org.lwjgl.vulkan.VK10
import vkk.VkResult
import vkk.entities.VkFence
import vkk.entities.VkSemaphore_Array
import vkk.entities.VkSwapchainKHR_Array
import vkk.extensions.PresentInfoKHR
import vkk.extensions.acquireNextImageKHR
import vkk.extensions.presentKHR
import vkk.vk10.resetFences
import vkk.vk10.structs.SubmitInfo
import vkk.vk10.submit
import vkk.vk10.waitForFences
import vulkan.OzDevice
import vulkan.OzVulkan

class DrawFrame(val ozVulkan: OzVulkan, val device: OzDevice, val frameLoop: FrameLoop) {

    val logger = KotlinLogging.logger { }

    fun draw() {
        var fps = 0L
        runBlocking {
            fps = frameLoop.fps.getTotal()
        }
        val index = (fps % ozVulkan.sync.max_frames_in_flight).toInt()
        ozVulkan.sync.wait(ozVulkan.sync.inFlightFences[index])



        val imageIndex = device.device.acquireNextImageKHR(
            swapchain = ozVulkan.swapchain.swapchain,
            timeout = -1L,
            semaphore = ozVulkan.sync.imageAvailable[index],
            fence = VkFence.NULL,
            check = {
                if (it == VkResult.ERROR_OUT_OF_DATE_KHR) {
                    logger.info("recreate swapchain/renderpass after acquire")
                    ozVulkan.recreateRenderpass(frameLoop.window.framebufferSize)
                } else if (it != VkResult.SUCCESS && it != VkResult.SUBOPTIMAL_KHR) {
                    logger.error { it.description }
                }
            }
//            check = ::defaultCheck
        )

        if (ozVulkan.sync.imagesInFlight[imageIndex] != VkFence.NULL) {
            ozVulkan.sync.wait(ozVulkan.sync.imagesInFlight[imageIndex])
        }
        ozVulkan.sync.imagesInFlight[imageIndex] = ozVulkan.sync.inFlightFences[index]

        val submitInfo = SubmitInfo(
            waitSemaphoreCount = 1,
            waitSemaphores = VkSemaphore_Array(arrayListOf(ozVulkan.sync.imageAvailable[index])),
            waitDstStageMask = intArrayOf(VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),
            commandBuffers = arrayOf(ozVulkan.commandBuffers.commandbuffers[imageIndex]),
            signalSemaphores = VkSemaphore_Array(arrayListOf(ozVulkan.sync.renderFinished[index]))
        )
        // That means that theoretically the implementation can already start executing our vertex shader and such while the image is not yet available.
        // Each entry in the waitStages array corresponds to the semaphore with the same index in pWaitSemaphores.


        device.device.resetFences(ozVulkan.sync.inFlightFences[index])

        device.graphicsQueue.submit(
            submit = submitInfo,
            fence = ozVulkan.sync.inFlightFences[index]
        )

        val presentInfoKHR = PresentInfoKHR(
            waitSemaphores = VkSemaphore_Array(arrayListOf(ozVulkan.sync.renderFinished[index])),
            swapchains = VkSwapchainKHR_Array(arrayListOf(ozVulkan.swapchain.swapchain)),
            imageIndices = intArrayOf(imageIndex),
            results = null
        )

        device.presentQueue.presentKHR(presentInfoKHR).also {
            if (it == VkResult.ERROR_OUT_OF_DATE_KHR || it == VkResult.SUBOPTIMAL_KHR) {
                logger.info("recreate swapchain/renderpass after present")
                ozVulkan.recreateRenderpass(frameLoop.window.framebufferSize)
            } else if (it != VkResult.SUCCESS) {
                logger.error("present swapchain fail")
            }
        }
        //also recreate swapchain when framebuffer resized
//        glfw.waitEvents()
//window minimization result in 0 size, wait to pause

    }
}