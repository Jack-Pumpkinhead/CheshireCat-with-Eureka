package vulkan.image

import vkk.*
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.Extent3D
import vkk.vk10.structs.ImageCreateInfo

/**
 * Created by CowardlyLion on 2020/7/18 23:05
 */

fun dstsampled(format: VkFormat, extent3D: Extent3D, mipLevels: Int = 1) = ImageCreateInfo(
    flags = VkImageCreate(0).i,
    imageType = VkImageType._2D,
    extent = extent3D,
    mipLevels = mipLevels,
    arrayLayers = 1,
    format = format,
    tiling = VkImageTiling.OPTIMAL,
    initialLayout = VkImageLayout.UNDEFINED,
    usage = VkImageUsage.TRANSFER_DST_BIT.or(VkImageUsage.SAMPLED_BIT),
    samples = VkSampleCount._1_BIT,
    sharingMode = VkSharingMode.EXCLUSIVE,
    queueFamilyIndices = null   //with sharingMode
)
fun sampled(format: VkFormat, extent3D: Extent3D, mipLevels: Int = 1) = ImageCreateInfo(
    flags = VkImageCreate(0).i,
    imageType = VkImageType._2D,
    extent = extent3D,
    mipLevels = mipLevels,
    arrayLayers = 1,
    format = format,
    tiling = VkImageTiling.OPTIMAL,
    initialLayout = VkImageLayout.UNDEFINED,
    usage = VkImageUsage.TRANSFER_SRC_BIT.or(VkImageUsage.TRANSFER_DST_BIT).or(VkImageUsage.SAMPLED_BIT),
    samples = VkSampleCount._1_BIT,
    sharingMode = VkSharingMode.EXCLUSIVE,
    queueFamilyIndices = null   //with sharingMode
)


fun depth(format: VkFormat, extent2D: Extent2D) = ImageCreateInfo(
    flags = VkImageCreate(0).i,
    imageType = VkImageType._2D,
    extent = Extent3D(extent2D, 1),
    mipLevels = 1,
    arrayLayers = 1,
    format = format,
    tiling = VkImageTiling.OPTIMAL,
    initialLayout = VkImageLayout.UNDEFINED,
    usage = VkImageUsage.DEPTH_STENCIL_ATTACHMENT_BIT.i,
    samples = VkSampleCount._1_BIT,
    sharingMode = VkSharingMode.EXCLUSIVE,
    queueFamilyIndices = null   //with sharingMode
)
