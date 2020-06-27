package vulkan

import vkk.VkImageUsage
import vkk.VkSharingMode
import vkk.entities.VkImage
import vkk.entities.VkImageView
import vkk.entities.VkSwapchainKHR
import vkk.extensions.*
import vkk.vk10.structs.Extent2D
import vulkan.buffer.OzVMA
import vulkan.concurrent.DrawCmds_old
import vulkan.image.DepthImage
import vulkan.util.SurfaceSwapchainSupport

class OzSwapchain(
    val device: OzDevice,
    sss: SurfaceSwapchainSupport,
    extent2D: Extent2D,
    oldSwapchain: VkSwapchainKHR,
    vma: OzVMA,
    commandPools: OzCommandPools,
    queues: OzQueues,
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
    val drawCmds: List<DrawCmds_old>
    val imageViews: List<VkImageView>
    val depth: List<DepthImage>

    init {
        val imagesArray = device.device.getSwapchainImagesKHR(swapchain)
        images = List(imagesArray.size) { imagesArray[it] }
        drawCmds = List(imagesArray.size) { DrawCmds_old(imagesArray[it]) }
        imageViews = images.map { ozImageViews.createColor(it, format) }
        depth = List(images.size){ DepthImage(vma, device, commandPools, queues, extent2D, ozImageViews) }
    }





    fun destroy() {
        depth.forEach {
            it.destroy()
        }
        imageViews.forEach {
            device.device.destroy(it)
        }
        device.device.destroy(swapchain)

        OzVulkan.logger.info {
            "swapchain destroyed"
        }
    }
}