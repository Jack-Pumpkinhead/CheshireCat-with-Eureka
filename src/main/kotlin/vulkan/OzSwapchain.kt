package vulkan

import glm_.vec2.Vec2i
import mu.KotlinLogging
import vkk.*
import vkk.entities.VkImage
import vkk.entities.VkImage_Array
import vkk.entities.VkSwapchainKHR
import vkk.extensions.*
import vkk.vk10.structs.Extent2D
import vulkan.util.SurfaceSwapchainSupport

class OzSwapchain(
    val ozVulkan: OzVulkan,
    val sss: SurfaceSwapchainSupport,
    val device: OzDevice,
    windowSize: Vec2i,
    oldSwapchain: VkSwapchainKHR = VkSwapchainKHR.NULL
) {

    val logger = KotlinLogging.logger { }

//    val format = chooseSwapSurfaceFormat(sss.formats)
//    val present = chooseSwapPresentMode(sss.presentModes)
    val extent =
//        if (sss.capabilities.currentExtent.width != Int.MAX_VALUE) {
//            sss.capabilities.currentExtent
//        } else Extent2D(windowSize)
        Extent2D(windowSize)

    val swapchainCIKHR: SwapchainCreateInfoKHR = SwapchainCreateInfoKHR(
        surface = sss.surface,
        minImageCount = sss.imageCount,
        imageFormat = sss.surfaceFormat.format,
        imageColorSpace = sss.surfaceFormat.colorSpace,
        imageExtent = extent,
        imageArrayLayers = 1,
        imageUsage = VkImageUsage.COLOR_ATTACHMENT_BIT.i,
        clipped = true,
        compositeAlpha = VkCompositeAlphaKHR.OPAQUE_BIT,
        imageSharingMode = VkSharingMode.EXCLUSIVE,
        preTransform = sss.capabilities.currentTransform,
        presentMode = sss.presentMode,
        oldSwapchain = oldSwapchain
    )

    val swapchain: VkSwapchainKHR

    val images: List<VkImage>

    init {
//        if (sss.queuefamily_graphic != sss.queuefamily_present) {
            swapchainCIKHR.imageSharingMode = VkSharingMode.CONCURRENT
            swapchainCIKHR.queueFamilyIndices = intArrayOf(
                sss.queuefamily_graphic,
//                sss.queuefamily_present,
                sss.queuefamily_transfer
            )
//        }

        swapchain = device.device.createSwapchainKHR(swapchainCIKHR)

        val imagesArray = device.device.getSwapchainImagesKHR(swapchain)
        images = List(imagesArray.size) { imagesArray[it] }


        logger.info {
            "images: ${images.size}"
        }
    }

    /*
    *  cleanup
    * */

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        device.device.destroy(swapchain)
    }
}