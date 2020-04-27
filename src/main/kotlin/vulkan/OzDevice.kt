package vulkan

import com.google.common.collect.Multisets
import com.google.common.collect.SortedMultiset
import com.google.common.collect.TreeMultiset
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

    val graphicI = 0
    val presentI: Int
    val transferI: Int
    val queueCIs: List<DeviceQueueCreateInfo>
    init {
        val set = TreeMultiset.create<Int>()
        set.add(surfaceSupport.queuefamily_graphic)

        presentI = set.count(surfaceSupport.queuefamily_present)
        set.add(surfaceSupport.queuefamily_present)

        transferI = set.count(surfaceSupport.queuefamily_transfer)
        set.add(surfaceSupport.queuefamily_graphic)

        val q = mutableListOf<DeviceQueueCreateInfo>()
        set.forEachEntry { index, count ->
            q += DeviceQueueCreateInfo(
                queueFamilyIndex = index,
                queuePriorities = FloatArray(count) { 0.5f }) //Specify the number of queues in this queueFamilyIndex to create. will work even slightly larger than queueFamProp[0].queueCount
        }
        queueCIs = q.toList()
    }

    val deviceCI = DeviceCreateInfo(
        queueCreateInfos = queueCIs,
        enabledExtensionNames = Extensions,
        enabledFeatures = ozPhysicalDevice.features
    )
    val device = ozPhysicalDevice.pd.createDevice(deviceCI)
    val graphicsQueue : Queue = device.getQueue(surfaceSupport.queuefamily_graphic,queueIndex = graphicI)
    val presentQueue: Queue = device.getQueue(surfaceSupport.queuefamily_present,queueIndex = presentI)
    val transferQueue: Queue = device.getQueue(surfaceSupport.queuefamily_transfer, queueIndex = transferI)

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(ozPhysicalDevice.ozInstance::destroy, this::destroy)
    }

    fun destroy() {
        device.destroy()
    }


}