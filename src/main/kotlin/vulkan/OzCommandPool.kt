package vulkan

import mu.KotlinLogging
import vkk.entities.VkCommandPool
import vkk.vk10.createCommandPool
import vkk.vk10.structs.CommandPoolCreateInfo
import vulkan.util.SurfaceSwapchainSupport

class OzCommandPool(val ozVulkan: OzVulkan, val device: OzDevice, val surfaceSupport: SurfaceSwapchainSupport) {

    val logger = KotlinLogging.logger { }

    val commandpool: VkCommandPool

    init {
        val queuefamily_graphic = surfaceSupport.queuefamily_graphic
        val commandPoolCI = CommandPoolCreateInfo(
            queueFamilyIndex = queuefamily_graphic,
            flags = 0
        )
        commandpool = device.device.createCommandPool(commandPoolCI)
    }


    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        device.device.destroy(commandpool)
    }

}