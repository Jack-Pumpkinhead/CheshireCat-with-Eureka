package vulkan

import game.main.OzConstants.Extensions
import mu.KotlinLogging
import vkk.identifiers.Queue
import vkk.vk10.createDevice
import vkk.vk10.features
import vkk.vk10.getQueue
import vkk.vk10.structs.DeviceCreateInfo
import vkk.vk10.structs.DeviceQueueCreateInfo
import vulkan.util.SurfaceSwapchainSupport
import kotlin.random.Random

class OzDevice(
    val ozVulkan: OzVulkan,
    val ozPhysicalDevice: OzPhysicalDevice,
    val surfaceSupport: SurfaceSwapchainSupport
) {

    val logger = KotlinLogging.logger { }

    val deviceQueueCI_graphics = DeviceQueueCreateInfo(
        queueFamilyIndex = surfaceSupport.queuefamily_graphic,
        queuePriorities = floatArrayOf(Random.nextFloat())
    )
    val deviceQueueCI_presentation = DeviceQueueCreateInfo(
        queueFamilyIndex = surfaceSupport.queuefamily_present,
        queuePriorities = floatArrayOf(Random.nextFloat())
    )
    val deviceCI = DeviceCreateInfo(
        queueCreateInfos = listOf(deviceQueueCI_graphics, deviceQueueCI_presentation),
        enabledExtensionNames = Extensions,
        enabledFeatures = ozPhysicalDevice.pd.features
    )
    val device = ozPhysicalDevice.pd.createDevice(deviceCI)
    val graphicsQueue : Queue = device.getQueue(deviceQueueCI_graphics.queueFamilyIndex)
    val presentQueue: Queue = device.getQueue(deviceQueueCI_presentation.queueFamilyIndex)

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(ozPhysicalDevice.ozInstance::destroy, this::destroy)
    }

    fun destroy() {
        device.destroy()
    }
}