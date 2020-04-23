package vulkan

import mu.KotlinLogging
import vkk.VkImageAspect
import vkk.VkImageViewType
import vkk.entities.VkImageView_Array
import vkk.entities.VkImage_Array
import vkk.extensions.getSwapchainImagesKHR
import vkk.vk10.createImageViewArray
import vkk.vk10.structs.ImageSubresourceRange
import vkk.vk10.structs.ImageViewCreateInfo

class OzImageViews(val ozVulkan: OzVulkan, val device: OzDevice, val swapchain: OzSwapchain) {

    val logger = KotlinLogging.logger { }


    val imageViewCI = ImageViewCreateInfo(
        viewType = VkImageViewType._2D,
        format = swapchain.swapchainCIKHR.imageFormat,
        subresourceRange = ImageSubresourceRange(
            aspectMask = VkImageAspect.COLOR_BIT.i,
            baseMipLevel = 0,
            levelCount = 1,
            baseArrayLayer = 0,
            layerCount = 1
        )
    )
    val imageViews: VkImageView_Array = device.device.createImageViewArray(imageViewCI, swapchain.images)



    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(swapchain::destroy, this::destroy)
    }

    fun destroy() {
        imageViews.indices.forEach { device.device.destroy(imageViews[it]) }
    }

}