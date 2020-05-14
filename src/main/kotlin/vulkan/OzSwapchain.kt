package vulkan

import vkk.VkImageUsage
import vkk.VkSharingMode
import vkk.entities.VkSwapchainKHR
import vkk.extensions.*
import vkk.vk10.structs.Extent2D
import vulkan.concurrent.OzImage
import vulkan.util.SurfaceSwapchainSupport

class OzSwapchain(
    val device: OzDevice,
    sss: SurfaceSwapchainSupport,
    extent2D: Extent2D,
    oldSwapchain: VkSwapchainKHR
) {

    val swapchainCIKHR: SwapchainCreateInfoKHR = SwapchainCreateInfoKHR(
        surface = sss.surface,
        minImageCount = sss.imageCount,
        imageFormat = sss.surfaceFormat.format,
        imageColorSpace = sss.surfaceFormat.colorSpace,
        imageExtent = extent2D,
        imageArrayLayers = 1,
        imageUsage = VkImageUsage.COLOR_ATTACHMENT_BIT.i,
        clipped = true,
        compositeAlpha = VkCompositeAlphaKHR.OPAQUE_BIT,
        imageSharingMode = VkSharingMode.CONCURRENT,
        queueFamilyIndices = intArrayOf(
            sss.queuefamily_graphic,
            sss.queuefamily_transfer
        ),
        preTransform = sss.capabilities.currentTransform,
        presentMode = sss.presentMode,
        oldSwapchain = oldSwapchain //Providing a valid oldSwapchain may aid in the resource reuse, and also allows the application to still present any images that are already acquired from it.
    )

    val swapchain: VkSwapchainKHR = device.device.createSwapchainKHR(swapchainCIKHR)


    val images: List<OzImage>

    init {
        val imagesArray = device.device.getSwapchainImagesKHR(swapchain)
        images = List(imagesArray.size) { OzImage(imagesArray[it]) }
    }





    fun destroy() {
        device.device.destroy(swapchain)
        OzVulkan.logger.info {
            "swapchain destroyed"
        }
    }
}