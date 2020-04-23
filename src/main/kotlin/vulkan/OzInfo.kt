package vulkan

import game.main.OzConstants
import game.main.OzConstants.InstanceExtensions
import game.main.OzConstants.Layers
import mu.KotlinLogging
import vkk.vk
import vkk.vk10.enumerateInstanceExtensionProperties
import vkk.vk10.instanceLayerProperties

object OzInfo {

    val logger = KotlinLogging.logger { }

    val AllInstanceExtensions = vk.enumerateInstanceExtensionProperties()

    val AllLayers = vk.instanceLayerProperties



    fun printInstanceExtensions() {
        logger.info("all instanceExts:")
        AllInstanceExtensions.forEach { logger.info("\t${it.extensionName}") }
        logger.info("enabled instanceExts:")
        InstanceExtensions.forEach { logger.info("\t$it") }
    }

    fun checkValidationLayerSupport() {
        logger.info("all layers:")
        AllLayers.forEach { logger.info("\t${it.layerName}") }
        logger.info("enabled layer:")
        Layers.forEach { logger.info("\t$it") }
    }
}