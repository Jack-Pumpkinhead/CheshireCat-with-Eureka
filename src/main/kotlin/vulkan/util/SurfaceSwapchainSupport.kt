package vulkan.util

import mu.KotlinLogging
import vkk.VkQueueFlag
import vkk.entities.VkPresentModeKHR_Array
import vkk.entities.VkSurfaceKHR
import vkk.extensions.*
import vkk.has
import vkk.identifiers.PhysicalDevice
import vkk.vk10.queueFamilyProperties

class SurfaceSwapchainSupport(val pd: PhysicalDevice, val surface: VkSurfaceKHR) {

    val logger = KotlinLogging.logger { }

    val capabilities: SurfaceCapabilitiesKHR = pd getSurfaceCapabilitiesKHR surface
    val formats: ArrayList<SurfaceFormatKHR> = pd getSurfaceFormatsKHR      surface
    val presentModes: VkPresentModeKHR_Array = pd getSurfacePresentModesKHR surface

    val queuefamily_graphic = pd.queueFamilyProperties.indexOfFirst { it.queueFlags has VkQueueFlag.GRAPHICS_BIT }
    val queuefamily_present = pd.queueFamilyProperties.indices.indexOfFirst { pd.getSurfaceSupportKHR(it, surface) }

    fun supported() =
        formats.isNotEmpty() &&
                presentModes.size > 0 &&
                queuefamily_graphic != -1 &&
                queuefamily_present != -1

}