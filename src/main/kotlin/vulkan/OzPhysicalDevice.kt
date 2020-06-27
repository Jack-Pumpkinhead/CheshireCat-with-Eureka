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

    val depthFormat = findDepthFormat() //D32_SFLOAT_S8_UINT

    init {
        OzVulkan.logger.info {
            depthFormat
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

}

fun VkFormat.hasStencil(): Boolean {
    return this == VkFormat.D32_SFLOAT_S8_UINT ||
            this == VkFormat.D24_UNORM_S8_UINT
}