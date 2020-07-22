package vulkan

import game.main.OzConfigurations
import kotlinx.coroutines.runBlocking
import vkk.VkFormat
import vkk.VkImageUsage
import vkk.VkSharingMode
import vkk.entities.VkImage
import vkk.entities.VkImageView
import vkk.entities.VkSwapchainKHR
import vkk.extensions.*
import vkk.vk10.structs.Extent2D
import vulkan.buffer.OzVMA
import vulkan.image.*
import vulkan.util.SurfaceSwapchainSupport

class OzSwapchain(
    val device: OzDevice,
    sss: SurfaceSwapchainSupport,
    extent2D: Extent2D,
    oldSwapchain: VkSwapchainKHR,
    vma: OzVMA,
    commandPools: OzCommandPools,
    queues: OzQueues,
    ozImages: OzImages,
    ozImageViews: OzImageViews
) {

    val format = sss.surfaceFormat.format

    val swapchainCIKHR: SwapchainCreateInfoKHR = SwapchainCreateInfoKHR(
        surface = sss.surface,
        minImageCount = sss.imageCount,
        imageFormat = format,
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


    val images: List<VkImage>
    val imageViews: List<VkImageView>
    lateinit var depth: List<OzImage2>
    lateinit var MSAA: List<OzImage2>
    lateinit var depth_MSAA: List<OzImage2>

    init {
        val imagesArray = device.device.getSwapchainImagesKHR(swapchain)
        images = List(imagesArray.size) { imagesArray[it] }
        imageViews = images.map { ozImageViews.createColor(it, format) }
        runBlocking {
            depth = List(images.size){ ozImages.createDepth(device.physicalDevice.depthFormat, extent2D)}
            MSAA = List(images.size){ ozImages.create_MSAA(VkFormat.R8G8B8A8_SRGB, extent2D, OzConfigurations.MSAA)}
            depth_MSAA = List(images.size){ ozImages.create_MSAADepth(device.physicalDevice.depthFormat, extent2D, OzConfigurations.MSAA)}

        }
    }





    fun destroy() {
        depth_MSAA.forEach { device.destroy(it) }
        MSAA.forEach { device.destroy(it) }
        depth.forEach { device.destroy(it) }
        imageViews.forEach {
            device.device.destroy(it)
        }
        device.device.destroy(swapchain)

        OzVulkan.logger.info {
            "swapchain destroyed"
        }
    }
}