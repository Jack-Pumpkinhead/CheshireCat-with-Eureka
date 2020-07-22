package vulkan.image

import vkk.*
import vkk.entities.VkSampler
import vkk.vk10.createSampler
import vkk.vk10.structs.SamplerCreateInfo
import vulkan.OzDevice

/**
 * Created by CowardlyLion on 2020/5/30 23:01
 */
class Samplers(val device: OzDevice) {

    val sampler = device.device.createSampler(
        SamplerCreateInfo(
            flags = VkSamplerCreate(0).i,
            magFilter = VkFilter.LINEAR,
            minFilter = VkFilter.LINEAR,
            addressModeU = VkSamplerAddressMode.REPEAT,
            addressModeV = VkSamplerAddressMode.REPEAT,
            addressModeW = VkSamplerAddressMode.REPEAT,
            anisotropyEnable = true,
            maxAnisotropy = 16f,    //1.0f if anisotropyEnable is false
//            anisotropyEnable = false,
//            maxAnisotropy = 1f,    //1.0f if anisotropyEnable is false
            borderColor = VkBorderColor.INT_OPAQUE_BLACK,
            unnormalizedCoordinates = false,
            compareEnable = false,
            compareOp = VkCompareOp.ALWAYS,
            mipmapMode = VkSamplerMipmapMode.LINEAR,
            mipLodBias = 0f,
            minLod = 0f,    //Lod: Level of Detail
            maxLod = 0f
        )
    )
    val samplerNearest = device.device.createSampler(
        SamplerCreateInfo(
            flags = VkSamplerCreate(0).i,
            magFilter = VkFilter.NEAREST,
            minFilter = VkFilter.NEAREST,
            addressModeU = VkSamplerAddressMode.REPEAT,
            addressModeV = VkSamplerAddressMode.REPEAT,
            addressModeW = VkSamplerAddressMode.REPEAT,
            anisotropyEnable = true,
            maxAnisotropy = 16f,
            borderColor = VkBorderColor.INT_OPAQUE_BLACK,
            unnormalizedCoordinates = false,
            compareEnable = false,
            compareOp = VkCompareOp.ALWAYS,
            mipmapMode = VkSamplerMipmapMode.NEAREST,
            mipLodBias = 0f,
            minLod = 0f,
            maxLod = 0f
        )
    )


    val samplerMipmap = Array<VkSampler?>(23) { null }

    fun samplerMipmap(mipLevels: Int): VkSampler {
        if (samplerMipmap[mipLevels] == null) {
            samplerMipmap[mipLevels] = device.device.createSampler(
                SamplerCreateInfo(
                    flags = VkSamplerCreate(0).i,
                    magFilter = VkFilter.LINEAR,
                    minFilter = VkFilter.LINEAR,
                    addressModeU = VkSamplerAddressMode.REPEAT,
                    addressModeV = VkSamplerAddressMode.REPEAT,
                    addressModeW = VkSamplerAddressMode.REPEAT,
                    anisotropyEnable = true,
                    maxAnisotropy = 16f,
                    borderColor = VkBorderColor.INT_OPAQUE_BLACK,
                    unnormalizedCoordinates = false,
                    compareEnable = false,
                    compareOp = VkCompareOp.ALWAYS,
                    mipmapMode = VkSamplerMipmapMode.LINEAR,
                    mipLodBias = 0f,
                    minLod = 0f,    //Lod: Level of Detail
                    maxLod = mipLevels.toFloat()
                )
            )
        }
        return samplerMipmap[mipLevels]!!
    }


    val samplerNearestMipmap = Array<VkSampler?>(23) { null }

    fun samplerNearestMipmap(mipLevels: Int): VkSampler {
        if (samplerNearestMipmap[mipLevels] == null) {
            samplerNearestMipmap[mipLevels] = device.device.createSampler(
                SamplerCreateInfo(
                    flags = VkSamplerCreate(0).i,
                    magFilter = VkFilter.NEAREST,
                    minFilter = VkFilter.NEAREST,
                    addressModeU = VkSamplerAddressMode.REPEAT,
                    addressModeV = VkSamplerAddressMode.REPEAT,
                    addressModeW = VkSamplerAddressMode.REPEAT,
                    anisotropyEnable = true,
                    maxAnisotropy = 16f,    //1.0f if anisotropyEnable is false
                    //            anisotropyEnable = false,
                    //            maxAnisotropy = 1f,    //1.0f if anisotropyEnable is false
                    borderColor = VkBorderColor.INT_OPAQUE_BLACK,
                    unnormalizedCoordinates = false,
                    compareEnable = false,
                    compareOp = VkCompareOp.ALWAYS,
                    mipmapMode = VkSamplerMipmapMode.NEAREST,
                    mipLodBias = 0f,
                    minLod = 0f,
                    maxLod = mipLevels.toFloat()
                )
            )
        }
        return samplerNearestMipmap[mipLevels]!!
    }


    fun destroy() {
        device.device.destroy(sampler)
        device.device.destroy(samplerNearest)
        samplerMipmap.forEach {
            if (it != null) {
                device.device.destroy(it)
            }
        }
        samplerNearestMipmap.forEach {
            if (it != null) {
                device.device.destroy(it)
            }
        }

    }
}