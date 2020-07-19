package vulkan.image

import kotlinx.coroutines.runBlocking
import vkk.entities.VkImageView
import vkk.vk10.structs.*
import vulkan.OzCommandPools
import vulkan.OzDevice
import vulkan.OzImageViews
import vulkan.OzQueues
import vulkan.buffer.OzVMA
import vulkan.command.TransitionImageLayout

/**
 * Created by CowardlyLion on 2020/6/2 22:49
 */
class DepthImage(
    val vma: OzVMA,
    val device: OzDevice,
    val commandPools: OzCommandPools,
    val queues: OzQueues,
    val extent2D: Extent2D,
    val ozImageViews: OzImageViews
) {
    val format = device.physicalDevice.depthFormat
    val image: VmaImage
    val imageView: VkImageView

    init {
        image = runBlocking {
//            vma.createImage_deviceLocal_depth(format, extent2D)
            vma.createImage_deviceLocal(depth(format, extent2D))
        }
        imageView = ozImageViews.depth(image.vkImage, format)


        runBlocking {
            optional()
        }
    }

    suspend fun optional() {

        val deferred = commandPools.graphicMutableCP.allocate()
        val cb = deferred.await()
        TransitionImageLayout.transitionImageLayout_depth(image.vkImage, format, cb)

        val submitT = queues.graphicQ_2.submit(
            SubmitInfo(
                commandBuffers = arrayOf(cb)
            )
        )
        submitT.await()

        commandPools.graphicMutableCP.free(cb)
    }

    fun destroy() {
        image.destroy()
        device.device.destroy(imageView)
    }
}