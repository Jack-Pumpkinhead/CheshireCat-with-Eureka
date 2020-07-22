package vulkan

import com.google.common.collect.TreeMultiset
import game.main.OzConstants.Extensions
import kotlinx.coroutines.*
import vkk.*
import vkk.entities.VkSemaphore
import vkk.identifiers.Queue
import vkk.vk10.*
import vkk.vk10.structs.CommandPoolCreateInfo
import vkk.vk10.structs.DeviceCreateInfo
import vkk.vk10.structs.DeviceQueueCreateInfo
import vkk.vk10.structs.FenceCreateInfo
import vulkan.concurrent.OzCommandPool
import vulkan.concurrent.OzQueue
import vulkan.image.OzImage2
import vulkan.util.SurfaceSwapchainSupport

class OzDevice(
    val physicalDevice: OzPhysicalDevice,
    val surfaceSupport: SurfaceSwapchainSupport
) {

    val multiset = TreeMultiset.create<Int>()

    fun nextQueue(queueFamily: Int): Pair<Int, Int> {
        val queueIndex = multiset.count(queueFamily) % physicalDevice.queueFamilyProperties[queueFamily].queueCount
        multiset += queueFamily
        return queueFamily to queueIndex
    }

    val graphicI = nextQueue(surfaceSupport.queuefamily_graphic)
    val graphicI_2 = nextQueue(surfaceSupport.queuefamily_graphic)
    val presentI = nextQueue(surfaceSupport.queuefamily_present)
    val transferI = nextQueue(surfaceSupport.queuefamily_transfer)

    val queueIs = listOf(
        graphicI,
        graphicI_2,
        presentI,
        transferI
    )

    val queueCIs: List<DeviceQueueCreateInfo> = multiset.entrySet().map {
        DeviceQueueCreateInfo(
            queueFamilyIndex = it.element,
            queuePriorities = FloatArray(it.count) { 0.5f }) //Specify the number of queues in this queueFamilyIndex to create. will work even slightly larger than queueFamProp[0].queueCount
    }

    val deviceCI = DeviceCreateInfo(
        queueCreateInfos = queueCIs,
        enabledExtensionNames = Extensions,
        enabledFeatures = physicalDevice.features
    )

    val device = physicalDevice.pd.createDevice(deviceCI)

    val scope = CoroutineScope(Dispatchers.Default)//scope before queueMap

    //prevent duplicate queue
    val queueMap = mutableMapOf<Pair<Int, Int>, OzQueue>()

    init {
        queueIs.forEach {
            queueMap.putIfAbsent(it, OzQueue(this, device.getQueue(it.first, it.second)))
        }
    }


    fun semaphore(): VkSemaphore = device.createSemaphore()
    fun signaledFence(flag: VkFenceCreateFlags = VkFenceCreate.SIGNALED_BIT.i) = device.createFence(FenceCreateInfo(flag))




    init {
        checkFormatProperties(VkFormat.R8G8B8A8_SRGB)
    }

    fun checkFormatProperties(format: VkFormat) {
        val formatProperties = physicalDevice.formatProperties(format)
        if (!formatProperties.optimalTilingFeatures.has(VkFormatFeature.SAMPLED_IMAGE_FILTER_LINEAR_BIT)) {
            OzVulkan.logger.error { "optimalTilingFeatures don't have VkFormatFeature.SAMPLED_IMAGE_FILTER_LINEAR_BIT !" }
        }

    }

    fun destroy() {
        device.waitIdle()
        device.destroy()
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

    fun destroy(image: OzImage2) {
        device.destroy(image.imageView)
        image.image.destroy()
    }


}