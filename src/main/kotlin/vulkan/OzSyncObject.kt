package vulkan

import land.oz.turorial.HelloTriangle
import mu.KotlinLogging
import vkk.VkFenceCreate
import vkk.entities.VkFence
import vkk.entities.VkSemaphore
import vkk.vk10.createFence
import vkk.vk10.createSemaphore
import vkk.vk10.structs.FenceCreateInfo
import vkk.vk10.structs.SemaphoreCreateInfo
import vkk.vk10.waitForFences

class OzSyncObject(val ozVulkan: OzVulkan, val device: OzDevice, swapchain: OzSwapchain) {

    val logger = KotlinLogging.logger { }

    val max_frames_in_flight = 2
    val imageAvailable: Array<VkSemaphore>
    val renderFinished: Array<VkSemaphore>

    val inFlightFences: Array<VkFence>
    val imagesInFlight: Array<VkFence>

    init {
        val semaphoreCI = SemaphoreCreateInfo()
        imageAvailable = Array(max_frames_in_flight) {
            device.device.createSemaphore(semaphoreCI)
        }
        renderFinished = Array(max_frames_in_flight) {
            device.device.createSemaphore(semaphoreCI)
        }

        val fenceCI = FenceCreateInfo(
            flags = VkFenceCreate.SIGNALED_BIT.i
        )
        inFlightFences = Array(max_frames_in_flight) {
            device.device.createFence(fenceCI)
        }
        imagesInFlight = Array(swapchain.images.size) { VkFence.NULL }

    }

    infix fun wait(fence: VkFence) {
        device.device.waitForFences(fence, true, -1)

    }



    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        inFlightFences.forEach { device.device.destroy(it) }
        renderFinished.forEach { device.device.destroy(it) }
        imageAvailable.forEach { device.device.destroy(it) }
    }


}