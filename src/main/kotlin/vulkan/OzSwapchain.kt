package vulkan

import glm_.vec2.Vec2i
import mu.KotlinLogging
import vkk.*
import vkk.entities.VkImageView_Array
import vkk.entities.VkImage_Array
import vkk.entities.VkPresentModeKHR_Array
import vkk.entities.VkSwapchainKHR
import vkk.extensions.*
import vkk.vk10.createImageViewArray
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.ImageSubresourceRange
import vkk.vk10.structs.ImageViewCreateInfo
import vulkan.util.SurfaceSwapchainSupport

class OzSwapchain(
    val ozVulkan: OzVulkan,
    val sss: SurfaceSwapchainSupport,
    val device: OzDevice,
    windowSize: Vec2i,
    var oldSwapchain: OzSwapchain? = null
) {

    val logger = KotlinLogging.logger { }

    val format = chooseSwapSurfaceFormat(sss.formats)
    val present = chooseSwapPresentMode(sss.presentModes)
    val extent =
//        if (sss.capabilities.currentExtent.width != Int.MAX_VALUE) {
//            sss.capabilities.currentExtent
//        } else Extent2D(windowSize)
        Extent2D(windowSize)

    private val imageCount =
        (sss.capabilities.minImageCount + 1).coerceAtMost(
            sss.capabilities.maxImageCount
        )
    val swapchainCIKHR: SwapchainCreateInfoKHR = SwapchainCreateInfoKHR(
        surface = sss.surface,
        minImageCount = imageCount,
        imageFormat = format.format,
        imageColorSpace = format.colorSpace,
        imageExtent = extent,
        imageArrayLayers = 1,
        imageUsage = VkImageUsage.COLOR_ATTACHMENT_BIT.i,
        clipped = true,
        compositeAlpha = VkCompositeAlphaKHR.OPAQUE_BIT,
        imageSharingMode = VkSharingMode.EXCLUSIVE,
        preTransform = sss.capabilities.currentTransform,
        presentMode = present,
        oldSwapchain = oldSwapchain?.swapchain ?: VkSwapchainKHR.NULL
    )

    val swapchain: VkSwapchainKHR

    init {
        if (sss.queuefamily_graphic != sss.queuefamily_present) {
            swapchainCIKHR.imageSharingMode = VkSharingMode.CONCURRENT
            swapchainCIKHR.queueFamilyIndices = intArrayOf(
                sss.queuefamily_graphic,
                sss.queuefamily_present
            )
        }

        if (oldSwapchain != null) {
            device.device.waitIdle()
        }
        swapchain = device.device.createSwapchainKHR(swapchainCIKHR)
        oldSwapchain = null
    }
    val images: VkImage_Array = device.device.getSwapchainImagesKHR(swapchain)

    fun chooseSwapSurfaceFormat(surfaceFormats: List<SurfaceFormatKHR>): SurfaceFormatKHR =
        surfaceFormats.firstOrNull {
            it.format == VkFormat.B8G8R8A8_SRGB && it.colorSpace == VkColorSpaceKHR.SRGB_NONLINEAR_KHR
        } ?: surfaceFormats[0]

    fun chooseSwapPresentMode(presentmodes: VkPresentModeKHR_Array): VkPresentModeKHR =
        if (presentmodes.array.contains(VkPresentModeKHR.MAILBOX.i)) VkPresentModeKHR.MAILBOX
        else VkPresentModeKHR.FIFO


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