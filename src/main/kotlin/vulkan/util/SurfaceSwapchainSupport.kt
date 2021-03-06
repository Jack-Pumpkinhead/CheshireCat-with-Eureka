package vulkan.util

import mu.KLogger
import mu.KotlinLogging
import vkk.VkFormat
import vkk.VkQueueFlag
import vkk.entities.VkPresentModeKHR_Array
import vkk.entities.VkSurfaceKHR
import vkk.extensions.*
import vkk.has
import vulkan.OzPhysicalDevice

class SurfaceSwapchainSupport(val physicalDevice: OzPhysicalDevice, val surface: VkSurfaceKHR) {

    val capabilities: SurfaceCapabilitiesKHR = physicalDevice.pd getSurfaceCapabilitiesKHR surface
    val formats: ArrayList<SurfaceFormatKHR> = physicalDevice.pd getSurfaceFormatsKHR      surface
    val presentModes: VkPresentModeKHR_Array = physicalDevice.pd getSurfacePresentModesKHR surface

    val surfaceFormat = chooseSwapSurfaceFormat(formats)   //temp solution
    val presentMode = chooseSwapPresentMode(presentModes)

    fun chooseSwapSurfaceFormat(surfaceFormats: List<SurfaceFormatKHR>): SurfaceFormatKHR{
        if(surfaceFormats.isEmpty()) return SurfaceFormatKHR(VkFormat.B8G8R8A8_SRGB, VkColorSpaceKHR.SRGB_NONLINEAR_KHR)
        return surfaceFormats.firstOrNull {
            it.format == VkFormat.B8G8R8A8_SRGB && it.colorSpace == VkColorSpaceKHR.SRGB_NONLINEAR_KHR
        } ?: surfaceFormats[0]
    }

    fun chooseSwapPresentMode(presentmodes: VkPresentModeKHR_Array): VkPresentModeKHR {
        if(presentmodes.size==0) return VkPresentModeKHR.FIFO
        return if (presentmodes.array.contains(VkPresentModeKHR.MAILBOX.i)) VkPresentModeKHR.MAILBOX
        else VkPresentModeKHR.FIFO
    }


    val queuefamily_graphic = physicalDevice.queueFamilyProperties.indexOfFirst { it.queueFlags has VkQueueFlag.GRAPHICS_BIT }
    val queuefamily_present = physicalDevice.queueFamilyProperties.indices.indexOfFirst { physicalDevice.pd.getSurfaceSupportKHR(it, surface) }
    val queuefamily_transfer = physicalDevice.queueFamilyProperties.indexOfFirst { it.queueFlags has VkQueueFlag.TRANSFER_BIT }

    val queueFamilies = listOf(
        queuefamily_graphic,
        queuefamily_present,
        queuefamily_transfer
    )

    val imageCount =
        (capabilities.minImageCount + 1).coerceAtMost(
            capabilities.maxImageCount
        )




    fun supported() =
        formats.isNotEmpty() &&
                presentModes.size > 0 &&
                queuefamily_graphic != -1 &&
                queuefamily_present != -1 &&
                queuefamily_transfer != -1


}