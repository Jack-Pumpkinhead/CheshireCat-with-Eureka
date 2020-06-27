package vulkan.command

import org.lwjgl.vulkan.VK10
import vkk.*
import vkk.entities.VkBuffer
import vkk.entities.VkDeviceSize
import vkk.entities.VkImage
import vkk.identifiers.CommandBuffer
import vkk.vk10.begin
import vkk.vk10.copyBufferToImage
import vkk.vk10.pipelineBarrier
import vkk.vk10.structs.*
import vulkan.OzCommandPools
import vulkan.OzQueues
import vulkan.hasStencil

/**
 * Created by CowardlyLion on 2020/5/25 20:23
 */
class TransitionImageLayout(val commandPools: OzCommandPools, val queues: OzQueues) {

    companion object {

        fun transitionImageLayout_tranDst(
            image: VkImage,
            format: VkFormat = VkFormat.B8G8R8A8_SRGB,
            cb: CommandBuffer
        ) {

            cb.begin(
                CommandBufferBeginInfo(
                    flags = VkCommandBufferUsage.ONE_TIME_SUBMIT_BIT.i
                )
            )


            val imageMemoryBarrier = ImageMemoryBarrier(
                srcAccessMask = VkAccess(0).i,
                dstAccessMask = VkAccess.TRANSFER_WRITE_BIT.i,
                oldLayout = VkImageLayout.UNDEFINED,
                newLayout = VkImageLayout.TRANSFER_DST_OPTIMAL,
                srcQueueFamilyIndex = VK10.VK_QUEUE_FAMILY_IGNORED,
                dstQueueFamilyIndex = VK10.VK_QUEUE_FAMILY_IGNORED,
                image = image,
                subresourceRange = ImageSubresourceRange(
                    aspectMask = VkImageAspect.COLOR_BIT.i,
                    baseMipLevel = 0,
                    levelCount = 1,
                    baseArrayLayer = 0,
                    layerCount = 1
                )   //The image and subresourceRange specify the image that is affected and the specific part of the image.
            )

            cb.pipelineBarrier(
                srcStageMask = VkPipelineStage.TOP_OF_PIPE_BIT.i,
                dstStageMask = VkPipelineStage.TRANSFER_BIT.i,
                dependencyFlags = VkDependency(0).i,
                memoryBarriers = null,
                imageMemoryBarriers = arrayOf(imageMemoryBarrier),
                bufferMemoryBarriers = null
            )
            cb.end()
        }

        fun transitionImageLayout_ShaderRead(
            image: VkImage,
            format: VkFormat = VkFormat.B8G8R8A8_SRGB,
            cb: CommandBuffer
        ) {

            cb.begin(
                CommandBufferBeginInfo(
                    flags = VkCommandBufferUsage.ONE_TIME_SUBMIT_BIT.i
                )
            )


            val imageMemoryBarrier = ImageMemoryBarrier(
                srcAccessMask = VkAccess.TRANSFER_WRITE_BIT.i,
                dstAccessMask = VkAccess.SHADER_READ_BIT.i,
                oldLayout = VkImageLayout.TRANSFER_DST_OPTIMAL,
                newLayout = VkImageLayout.SHADER_READ_ONLY_OPTIMAL,
                srcQueueFamilyIndex = VK10.VK_QUEUE_FAMILY_IGNORED,
                dstQueueFamilyIndex = VK10.VK_QUEUE_FAMILY_IGNORED,
                image = image,
                subresourceRange = ImageSubresourceRange(
                    aspectMask = VkImageAspect.COLOR_BIT.i,
                    baseMipLevel = 0,
                    levelCount = 1,
                    baseArrayLayer = 0,
                    layerCount = 1
                )   //The image and subresourceRange specify the image that is affected and the specific part of the image.
            )

            cb.pipelineBarrier(
                srcStageMask = VkPipelineStage.TRANSFER_BIT.i,
                dstStageMask = VkPipelineStage.FRAGMENT_SHADER_BIT.i,
                dependencyFlags = VkDependency(0).i,
                memoryBarriers = null,
                imageMemoryBarriers = arrayOf(imageMemoryBarrier),
                bufferMemoryBarriers = null
            )
            cb.end()


        }

        fun transitionImageLayout_depth(
            image: VkImage,
            format: VkFormat,
            cb: CommandBuffer
        ) {

            cb.begin(
                CommandBufferBeginInfo(
                    flags = VkCommandBufferUsage.ONE_TIME_SUBMIT_BIT.i
                )
            )


            val imageMemoryBarrier = ImageMemoryBarrier(
                srcAccessMask = 0,
                dstAccessMask =
                    VkAccess.DEPTH_STENCIL_ATTACHMENT_READ_BIT.or(
                    VkAccess.DEPTH_STENCIL_ATTACHMENT_WRITE_BIT
                ),
                oldLayout = VkImageLayout.UNDEFINED,
                newLayout = VkImageLayout.DEPTH_STENCIL_ATTACHMENT_OPTIMAL,
                srcQueueFamilyIndex = VK10.VK_QUEUE_FAMILY_IGNORED,
                dstQueueFamilyIndex = VK10.VK_QUEUE_FAMILY_IGNORED,
                image = image,
                subresourceRange = ImageSubresourceRange(
                    aspectMask =
                    if (format.hasStencil()) {
                        VkImageAspect.DEPTH_BIT.or(VkImageAspect.STENCIL_BIT)
                    } else {
                        VkImageAspect.DEPTH_BIT.i
                    },
                    baseMipLevel = 0,
                    levelCount = 1,
                    baseArrayLayer = 0,
                    layerCount = 1
                )   //The image and subresourceRange specify the image that is affected and the specific part of the image.
            )

            cb.pipelineBarrier(
                srcStageMask = VkPipelineStage.TOP_OF_PIPE_BIT.i,
                dstStageMask = VkPipelineStage.EARLY_FRAGMENT_TESTS_BIT.i,
//                dstStageMask = VkPipelineStage.TOP_OF_PIPE_BIT.i,
                dependencyFlags = VkDependency(0).i,
                memoryBarriers = null,
                imageMemoryBarriers = arrayOf(imageMemoryBarrier),
                bufferMemoryBarriers = null
            )
            cb.end()


        }


        fun copyBufferToImage(buffer: VkBuffer, image: VkImage, width: Int, height: Int, cb: CommandBuffer) {
            cb.begin(
                CommandBufferBeginInfo(
                    flags = VkCommandBufferUsage.ONE_TIME_SUBMIT_BIT.i
                )
            )
            val bufferImageCopy = BufferImageCopy(
                bufferOffset = VkDeviceSize(0),
                bufferRowLength = 0,
                bufferImageHeight = 0,  //Specifying 0 for both indicates that the pixels are simply tightly packed
                imageSubresource = ImageSubresourceLayers(
                    aspectMask = VkImageAspect.COLOR_BIT.i,
                    mipLevel = 0,
                    baseArrayLayer = 0,
                    layerCount = 1
                ), imageOffset = Offset3D(0, 0, 0), imageExtent = Extent3D(
                    width,
                    height,
                    1
                )
            )
            cb.copyBufferToImage(
                srcBuffer = buffer,
                dstImage = image,
                dstImageLayout = VkImageLayout.TRANSFER_DST_OPTIMAL,
                regions = arrayOf(bufferImageCopy)
            )
            cb.end()
        }

    }


}