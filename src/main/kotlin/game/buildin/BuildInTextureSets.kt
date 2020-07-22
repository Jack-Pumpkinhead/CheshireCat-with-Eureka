package game.buildin

import kotlinx.coroutines.runBlocking
import vulkan.concurrent.SyncArray2
import vulkan.image.OzImages
import vulkan.image.Samplers
import vulkan.pipelines.descriptor.SingleTextureSets

/**
 * Created by CowardlyLion on 2020/7/21 23:12
 */
class BuildInTextureSets(
    val images: OzImages,
    val samplers: Samplers,
    val singleTextureSets: SingleTextureSets
) {
    val crafting_table = runBlocking {
        val texture = images.getTexture(OzImages.BuildIn.crafting_table.path)
        singleTextureSets.put(texture.imageView, samplers.samplerNearestMipmap(texture.createInfo.mipLevels))
    }
    val UVA = mipmap(OzImages.BuildIn.              UVA)
    val UVB = mipmap(OzImages.BuildIn.              UVB)
    val Icosphere_blue = mipmap(OzImages.BuildIn.   Icosphere_blue)
    val Icosphere_green = mipmap(OzImages.BuildIn.  Icosphere_green)
    val Icosphere_red = mipmap(OzImages.BuildIn.    Icosphere_red)
    val Icosphere_yellow = mipmap(OzImages.BuildIn. Icosphere_yellow)
    val noise = mipmap(OzImages.BuildIn.            noise)

    fun mipmap(aaa: OzImages.BuildIn): SyncArray2<SingleTextureSets.ImageInfo>.InArr {
        return runBlocking {
            val texture = images.getTexture(aaa.path)
            singleTextureSets.put(texture.imageView, samplers.samplerNearestMipmap(texture.createInfo.mipLevels))
        }
    }

}