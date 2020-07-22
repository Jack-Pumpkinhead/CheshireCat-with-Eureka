package vulkan

import game.main.OzConstants
import vkk.*
import vkk.identifiers.PhysicalDevice
import vkk.vk10.*
import vkk.vk10.structs.ExtensionProperties
import vkk.vk10.structs.PhysicalDeviceFeatures
import vkk.vk10.structs.PhysicalDeviceProperties
import vkk.vk10.structs.QueueFamilyProperties

/**
 * Created by CowardlyLion on 2020/4/20 19:46
 */
class OzPhysicalDevice(val pd: PhysicalDevice) {

    val properties: PhysicalDeviceProperties = pd.properties
    val queueFamilyProperties: Array<QueueFamilyProperties> =pd.queueFamilyProperties
    val features: PhysicalDeviceFeatures = pd.features
    val extensions: List<String> = pd.enumerateDeviceExtensionProperties().map(ExtensionProperties::extensionName)
    fun formatProperties(format: VkFormat) = pd.getFormatProperties(format)

    val depthFormat = findDepthFormat() //D32_SFLOAT_S8_UINT
    val maxMSAA = maxMSAA()

    init {
        OzVulkan.logger.info {
            "depthFormat: $depthFormat, maxMSAA: ${maxMSAA.i}"
        }
        OzVulkan.logger.info {
            "device feature, sampleRateShading: ${features.sampleRateShading}"
        }

    }

    fun supported(): Boolean =
        properties.deviceType == VkPhysicalDeviceType.DISCRETE_GPU &&
                features.geometryShader &&
                features.samplerAnisotropy &&
                extensions.containsAll(OzConstants.Extensions)

    //    val min = properties.limits.minUniformBufferOffsetAlignment
    fun firstSupportedFormat(candidates: List<VkFormat>, tiling: VkImageTiling, features: VkFormatFeature): VkFormat {
        for (format in candidates) {

            val properties = pd.getFormatProperties(format)
            when (tiling) {
                VkImageTiling.LINEAR -> {
                    if (properties.linearTilingFeatures.has(features)) {
                        return format
                    }
                }
                VkImageTiling.OPTIMAL ->{
                    if (properties.optimalTilingFeatures.has(features)) {
                        return format
                    }
                }
            }
        }
        OzVulkan.logger.warn {
            "supported format not found"
        }
        return candidates[0]
    }
    fun findDepthFormat() = firstSupportedFormat(
        listOf(
            VkFormat.D32_SFLOAT,
            VkFormat.D32_SFLOAT_S8_UINT,
            VkFormat.D24_UNORM_S8_UINT
        ),
        VkImageTiling.OPTIMAL,
        VkFormatFeature.DEPTH_STENCIL_ATTACHMENT_BIT
    )

    fun maxMSAA(): VkSampleCount {
        val sampleCountFlags = properties.limits.framebufferColorSampleCounts.and(
            properties.limits.framebufferDepthSampleCounts
        )
        infix fun Int.has(b: VkSampleCount): Boolean = and(b.i) != 0
        if(sampleCountFlags.has(VkSampleCount._64_BIT)) return VkSampleCount._64_BIT
        if(sampleCountFlags.has(VkSampleCount._32_BIT)) return VkSampleCount._32_BIT
        if(sampleCountFlags.has(VkSampleCount._16_BIT)) return VkSampleCount._16_BIT
        if(sampleCountFlags.has(VkSampleCount._8_BIT)) return VkSampleCount._8_BIT
        if(sampleCountFlags.has(VkSampleCount._4_BIT)) return VkSampleCount._4_BIT
        if(sampleCountFlags.has(VkSampleCount._2_BIT)) return VkSampleCount._2_BIT
        return VkSampleCount._1_BIT
    }

}

fun VkFormat.hasStencil(): Boolean {
    return this == VkFormat.D32_SFLOAT_S8_UINT ||
            this == VkFormat.D24_UNORM_S8_UINT
}