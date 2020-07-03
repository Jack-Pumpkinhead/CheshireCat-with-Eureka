package vulkan.image

import game.input.SpringInput
import game.main.Univ
import kotlinx.coroutines.runBlocking
import vkk.*
import vkk.entities.VkImageView
import vkk.entities.VkSampler
import vkk.vk10.structs.*
import vulkan.*
import vulkan.buffer.OzVMA
import vulkan.command.TransitionImageLayout

/**
 * Created by CowardlyLion on 2020/5/24 23:16
 */
class OzImage(
    val vma: OzVMA,
    val device: OzDevice,
    val commandPools: OzCommandPools,
    val queues: OzQueues,
    val ozImageViews: OzImageViews,
    val springInput: SpringInput,
    val path: String,
    val sampler: VkSampler
) {
    constructor(univ:Univ,path: String,sampler: VkSampler):this(
        univ.vulkan.vma,
        univ.vulkan.device,
        univ.vulkan.commandpools,
        univ.vulkan.queues,
        univ.vulkan.imageViews,
        univ.springInput,
        path,
        sampler
    )

    val image: VmaImage
    val imageView: VkImageView


    init {
        image = runBlocking {
            getImage(path)
        }
        imageView = ozImageViews.createColor(image.vkImage, VkFormat.R8G8B8A8_SRGB)
    }

    private suspend fun getImage(path: String): VmaImage {

        val deferred = commandPools.graphicMutableCP.allocate(3)

        val texture = springInput.loadImage(path)

        val buffer = vma.createBuffer_imageStaging(texture.size)
        buffer.memory.fill(texture.data())

        val image = vma.createImage_deviceLocal_dstsampled(
//            VkFormat.B8G8R8A8_SRGB,   //?????BGRA
            VkFormat.R8G8B8A8_SRGB,
            Extent3D(texture.extent().x, texture.extent().y, texture.extent().z)
        )


        val cbs = deferred.await()
        val trans_dstOptimal = 0
        val copy = 1
        val trans_shaderRead = 2

        TransitionImageLayout.transitionImageLayout_tranDst(
            image = image.vkImage,
            cb = cbs[trans_dstOptimal]
        )

        TransitionImageLayout.copyBufferToImage(
            buffer = buffer.vkBuffer,
            image = image.vkImage,
            width = texture.extent().x,
            height = texture.extent().y,
            cb = cbs[copy]
        )
        TransitionImageLayout.transitionImageLayout_ShaderRead(
            image = image.vkImage,
            cb = cbs[trans_shaderRead]
        )

        val submitT = queues.graphicQ_2.submit(
            SubmitInfo(
                commandBuffers = cbs
            )
        )
        submitT.await()
        commandPools.graphicMutableCP.free(cbs)
        buffer.destroy()

        return image
    }


    fun destroy() {
        image.destroy()
        device.device.destroy(imageView)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}