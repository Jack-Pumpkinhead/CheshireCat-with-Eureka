package vulkan.image

import game.input.SpringInput
import game.main.OzConfigurations.textureGenerateMipmap
import vkk.VkFormat
import vkk.VkSampleCount
import vkk.entities.VkSampler
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.Extent3D
import vulkan.*
import vulkan.buffer.OzVMA
import vulkan.command.OzCommands
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log2
import kotlin.math.max

/**
 * Created by CowardlyLion on 2020/5/30 23:07
 */
class OzImages(
    val vma: OzVMA,
    val device: OzDevice,
    val commandPools: OzCommandPools,
    val queues: OzQueues,
    val ozImageViews: OzImageViews,
    val springInput: SpringInput,
    val samplers: Samplers,
    val commands: OzCommands
) {

    val list = mutableListOf<OzImage>()

    var index = 0
    fun put(path: String, sampler: VkSampler): Int {
        list += OzImage(
            vma, device, commandPools, queues, ozImageViews, springInput,
            path, sampler
        )
        return index++
    }


    //    val crafting_table = put("textures\\crafting_table.png", samplers.sampler_Mipmap(4))
//    val UVA = put("textures\\UVA.png", samplers.sampler)
//    val UVB = put("textures\\UVB.png", samplers.sampler)
//    val Icosphere_blue = put("textures\\Icosphere_blue.png", samplers.sampler)
//    val Icosphere_green = put("textures\\Icosphere_green.png", samplers.sampler)
//    val Icosphere_red = put("textures\\Icosphere_red.png", samplers.sampler)
//    val Icosphere_yellow = put("textures\\Icosphere_yellow.png", samplers.sampler)
//    val noise = put("textures\\noise.png", samplers.sampler)
    val crafting_table   = put("textures\\crafting_table.png",     samplers.samplerNearestMipmap(10))
    val UVA              = put(  "textures\\UVA.png",              samplers.samplerMipmap(10))
    val UVB              = put("textures\\UVB.png",                samplers.samplerMipmap(10))
    val Icosphere_blue   = put("textures\\Icosphere_blue.png",     samplers.samplerMipmap(10))
    val Icosphere_green  = put("textures\\Icosphere_green.png",    samplers.samplerMipmap(10))
    val Icosphere_red    = put("textures\\Icosphere_red.png",      samplers.samplerMipmap(10))
    val Icosphere_yellow = put("textures\\Icosphere_yellow.png",   samplers.samplerMipmap(10))
    val noise            = put("textures\\noise.png",              samplers.samplerMipmap(10))


    enum class BuildIn {
        crafting_table,
        UVA,
        UVB,
        Icosphere_blue,
        Icosphere_green,
        Icosphere_red,
        Icosphere_yellow,
        noise,
        ;
        val path = "textures\\$name.png"
    }

    val textures = ConcurrentHashMap<String,OzImage2>()

    suspend fun getTexture(path: String): OzImage2 {
        return if (textures.contains(path)) {
            textures[path]!!
        } else {
            val texture = loadTexture(path)
            textures[path] = texture
            texture
        }
    }

    suspend fun loadTexture(path: String): OzImage2 {
        val texture = springInput.loadImage(path)
        /*      1
        Univ.logger.info {
            "texture $path levels:   ${texture.levels()}"
        }*/

        val extent = texture.extent()
        val extent3D = Extent3D(extent.x, extent.y, extent.z)

        val mipLevels = if (textureGenerateMipmap) {
            log2(max(extent.x, extent.y).toFloat()).toInt() + 1
        } else 1


        val createInfo = dstsampled(
            VkFormat.R8G8B8A8_SRGB,
            extent3D,
            mipLevels
        )
        val image = vma.createImage_deviceLocal(createInfo)
        val imageView = ozImageViews.createColor(image.vkImage, VkFormat.R8G8B8A8_SRGB, mipLevels)

        commands.fillImage(texture, image, mipLevels, extent3D)
        return OzImage2(createInfo, image, imageView)
    }
    suspend fun createDepth(format: VkFormat, extent2D: Extent2D): OzImage2 {
        val createInfo = depth(format, extent2D)
        val image = vma.createImage_deviceLocal(createInfo)
        val imageView = ozImageViews.depth(image.vkImage, format)

        /*//optional
        val deferred = commandPools.graphicMutableCP.allocate()
        val cb = deferred.await()
        TransitionImageLayout.transitionImageLayout_depth(image.vkImage, format, cb)
        val submitT = queues.graphicQ_2.submit(
            SubmitInfo(commandBuffers = arrayOf(cb))
        )
        submitT.await()
        commandPools.graphicMutableCP.free(cb)
*/
        return OzImage2(createInfo, image, imageView)
    }
    fun create_MSAA(format: VkFormat, extent2D: Extent2D, samples: VkSampleCount): OzImage2 {
        val createInfo = multiSampling(format, extent2D, samples)
        val image = vma.createImage_deviceLocal(createInfo)
        val imageView = ozImageViews.createColor(image.vkImage, format)

        return OzImage2(createInfo, image, imageView)
    }
    fun create_MSAADepth(format: VkFormat, extent2D: Extent2D, samples: VkSampleCount): OzImage2 {
        val createInfo = depth_MSAA(format, extent2D, samples)
        val image = vma.createImage_deviceLocal(createInfo)
        val imageView = ozImageViews.depth(image.vkImage, format)

        return OzImage2(createInfo, image, imageView)
    }




    fun destroy() {
        list.forEach { it.destroy() }
        textures.forEachValue(2, device::destroy)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }
}