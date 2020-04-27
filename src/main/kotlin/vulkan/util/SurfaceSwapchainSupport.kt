package vulkan.util

import mu.KotlinLogging
import vkk.VkQueueFlag
import vkk.entities.VkPresentModeKHR_Array
import vkk.entities.VkSurfaceKHR
import vkk.extensions.*
import vkk.has
import vkk.identifiers.PhysicalDevice
import vkk.vk10.queueFamilyProperties
import vulkan.OzPhysicalDevice

class SurfaceSwapchainSupport(val physicalDevice: OzPhysicalDevice, val surface: VkSurfaceKHR) {

    val logger = KotlinLogging.logger { }

    val capabilities: SurfaceCapabilitiesKHR = physicalDevice.pd getSurfaceCapabilitiesKHR surface
    val formats: ArrayList<SurfaceFormatKHR> = physicalDevice.pd getSurfaceFormatsKHR      surface
    val presentModes: VkPresentModeKHR_Array = physicalDevice.pd getSurfacePresentModesKHR surface

    val queuefamily_graphic = physicalDevice.queueFamilyProperties.indexOfFirst { it.queueFlags has VkQueueFlag.GRAPHICS_BIT }
    val queuefamily_present = physicalDevice.queueFamilyProperties.indices.indexOfFirst { physicalDevice.pd.getSurfaceSupportKHR(it, surface) }
    val queuefamily_transfer = physicalDevice.queueFamilyProperties.indexOfFirst { it.queueFlags has VkQueueFlag.TRANSFER_BIT }

    init {
        logger.info {
            "queueFamProp[0].queueCount: ${physicalDevice.queueFamilyProperties[0].queueCount}"
        }
//        logger.info {
//            physicalDevice.queueFamilyProperties[0].queueFlags.has(VkQueueFlag.PROTECTED_BIT)
//        }
        logger.info {
            "graphic: $queuefamily_graphic\t" +
                    "present: $queuefamily_present\t" +
                    "transfer: $queuefamily_transfer"
        }
    }

    fun supported() =
        formats.isNotEmpty() &&
                presentModes.size > 0 &&
                queuefamily_graphic != -1 &&
                queuefamily_present != -1 &&
                queuefamily_transfer != -1


}