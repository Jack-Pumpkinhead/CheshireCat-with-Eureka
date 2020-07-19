package vulkan.image

import game.input.SpringInput
import game.main.Univ
import gli_.Texture
import kotlinx.coroutines.runBlocking
import vkk.*
import vkk.entities.VkImageView
import vkk.entities.VkSampler
import vkk.vk10.structs.*
import vulkan.*
import vulkan.buffer.OzVMA
import vulkan.command.TransitionImageLayout
import kotlin.math.log2
import kotlin.math.max

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
    val sampler: VkSampler,
    generateMipmap: Boolean = true
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
    val imageCI: ImageCreateInfo
    val imageView: VkImageView


    init {
        val texture = springInput.loadImage(path)
        /*      1
        Univ.logger.info {
            "texture $path levels:   ${texture.levels()}"
        }*/

        val extent = texture.extent()
        val mipLevels = if (generateMipmap) {
            log2(max(extent.x, extent.y).toFloat()).toInt() + 1
        } else 1


        imageCI = dstsampled(
            VkFormat.R8G8B8A8_SRGB,
            Extent3D(extent.x, extent.y, extent.z),
            mipLevels
        )
        image = vma.createImage_deviceLocal(imageCI)
        imageView = ozImageViews.createColor(image.vkImage, VkFormat.R8G8B8A8_SRGB, mipLevels)

        runBlocking {
            fillImage(texture, image, imageCI.mipLevels, imageCI.extent)
        }
    }

    suspend fun fillImage(texture: Texture, image: VmaImage, mipLevels: Int = 1, extent: Extent3D) {
        val deferred = commandPools.graphicMutableCP.allocate(3)
        val buffer = vma.createBuffer_imageStaging(texture.size)
        buffer.memory.fill(texture.data())

        val cbs = deferred.await()
        val trans_dstOptimal = 0
        val copy = 1
        val trans_shaderRead_mipmap = 2

        TransitionImageLayout.transitionImageLayout_tranDst(
            image = image.vkImage,
            cb = cbs[trans_dstOptimal],
            mipLevels = mipLevels
        )

        TransitionImageLayout.copyBufferToImage(
            buffer = buffer.vkBuffer,
            image = image.vkImage,
            width = extent.width,
            height = extent.height,
            cb = cbs[copy]
        )

        val formatProperties = device.physicalDevice.formatProperties(imageCI.format)
        if (!formatProperties.optimalTilingFeatures.has(VkFormatFeature.SAMPLED_IMAGE_FILTER_LINEAR_BIT)) {
            OzVulkan.logger.error { "optimalTilingFeatures don't have VkFormatFeature.SAMPLED_IMAGE_FILTER_LINEAR_BIT !" }
        }


        TransitionImageLayout.generateMipmaps(
            image = image.vkImage,
            cb = cbs[trans_shaderRead_mipmap],
            width = extent.width,
            height = extent.height,
            mipLevels = mipLevels
        )
        /*TransitionImageLayout.transitionImageLayout_ShaderRead(
            image = image.vkImage,
            cb = cbs[trans_shaderRead],
            mipLevel = mipLevels
        )*/

        val submitT = queues.graphicQ_2.submit(
            SubmitInfo(
                commandBuffers = cbs
            )
        )
        submitT.await()
        commandPools.graphicMutableCP.free(cbs)
        buffer.destroy()

    }


    fun destroy() {
        image.destroy()
        device.device.destroy(imageView)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}