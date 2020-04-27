package vulkan

import game.main.OzConstants
import mu.KotlinLogging
import vkk.VkPhysicalDeviceType
import vkk.identifiers.PhysicalDevice
import vkk.vk10.enumerateDeviceExtensionProperties
import vkk.vk10.features
import vkk.vk10.properties
import vkk.vk10.queueFamilyProperties
import vkk.vk10.structs.ExtensionProperties
import vkk.vk10.structs.PhysicalDeviceFeatures
import vkk.vk10.structs.PhysicalDeviceProperties
import vkk.vk10.structs.QueueFamilyProperties

/**
 * Created by CowardlyLion on 2020/4/20 19:46
 */
class OzPhysicalDevice(val ozVulkan: OzVulkan, val ozInstance: OzInstance, val pd: PhysicalDevice) {

    val logger = KotlinLogging.logger { }

    val properties: PhysicalDeviceProperties = pd.properties
    val queueFamilyProperties: Array<QueueFamilyProperties> =pd.queueFamilyProperties
    val features: PhysicalDeviceFeatures = pd.features
    val extensions: List<String> = pd.enumerateDeviceExtensionProperties().map(ExtensionProperties::extensionName)

    fun supported(): Boolean =
        properties.deviceType == VkPhysicalDeviceType.DISCRETE_GPU &&
                features.geometryShader &&
                extensions.containsAll(OzConstants.Extensions)

    fun printExtensions() {
        logger.info("exts of physical device: ${properties.deviceName}")
        extensions.forEach { logger.info("\t$it") }
    }

}