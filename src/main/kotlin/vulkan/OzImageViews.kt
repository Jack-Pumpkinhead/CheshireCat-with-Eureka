package vulkan

import vkk.VkFormat
import vkk.VkImageAspect
import vkk.VkImageViewCreate
import vkk.VkImageViewType
import vkk.entities.VkImage
import vkk.entities.VkImageView
import vkk.vk10.createImageView
import vkk.vk10.structs.ComponentMapping
import vkk.vk10.structs.ImageSubresourceRange
import vkk.vk10.structs.ImageViewCreateInfo

class OzImageViews(val device: OzDevice) {

    fun createColor(image: VkImage, format: VkFormat, mipLevels: Int = 1): VkImageView {
        return device.device.createImageView(
            ImageViewCreateInfo(
                flags = VkImageViewCreate(0).i,
                image = image,
                viewType = VkImageViewType._2D,
                format = format,
                components = ComponentMapping(),
                subresourceRange = ImageSubresourceRange(
                    aspectMask = VkImageAspect.COLOR_BIT.i,
                    baseMipLevel = 0,
                    levelCount = mipLevels,
                    baseArrayLayer = 0,
                    layerCount = 1
                )
            )
        )
    }
    fun depthStencil(image: VkImage, format: VkFormat): VkImageView {
        return device.device.createImageView(
            ImageViewCreateInfo(
                flags = VkImageViewCreate(0).i,
                image = image,
                viewType = VkImageViewType._2D,
                format = format,
                components = ComponentMapping(),
                subresourceRange = ImageSubresourceRange(
                    aspectMask = VkImageAspect.DEPTH_BIT.or(VkImageAspect.STENCIL_BIT),
                    baseMipLevel = 0,
                    levelCount = 1,
                    baseArrayLayer = 0,
                    layerCount = 1
                )
            )
        )
    }
    fun depth(image: VkImage, format: VkFormat): VkImageView {
        return device.device.createImageView(
            ImageViewCreateInfo(
                flags = VkImageViewCreate(0).i,
                image = image,
                viewType = VkImageViewType._2D,
                format = format,
                components = ComponentMapping(),
                subresourceRange = ImageSubresourceRange(
                    aspectMask = VkImageAspect.DEPTH_BIT.i,
                    baseMipLevel = 0,
                    levelCount = 1,
                    baseArrayLayer = 0,
                    layerCount = 1
                )
            )
        )
    }




    /*fun destroy() {
        imageViews.forEach {
            device.device.destroy(it)
        }
        OzVulkan.logger.info {
            "imageviews destroyed"
        }
    }*/

}