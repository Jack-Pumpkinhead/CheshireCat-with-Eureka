package vulkan

import mu.KotlinLogging
import vkk.VkImageAspect
import vkk.VkImageViewCreate
import vkk.VkImageViewType
import vkk.entities.VkImageView
import vkk.vk10.createImageView
import vkk.vk10.structs.ComponentMapping
import vkk.vk10.structs.ImageSubresourceRange
import vkk.vk10.structs.ImageViewCreateInfo

class OzImageViews(val device: OzDevice, val swapchain: OzSwapchain) {

    val imageViews: List<VkImageView> = swapchain.images.map {
        device.device.createImageView(
            ImageViewCreateInfo(
                flags = VkImageViewCreate(0).i,
                image = it.image,
                viewType = VkImageViewType._2D,
                format = swapchain.swapchainCIKHR.imageFormat,
                components = ComponentMapping(),
                subresourceRange = ImageSubresourceRange(
                    aspectMask = VkImageAspect.COLOR_BIT.i,
                    baseMipLevel = 0,
                    levelCount = 1,
                    baseArrayLayer = 0,
                    layerCount = 1
                )
            )
        )
    }


    fun destroy() {
        imageViews.forEach {
            device.device.destroy(it)
        }
        OzVulkan.logger.info {
            "imageviews destroyed"
        }
    }

}