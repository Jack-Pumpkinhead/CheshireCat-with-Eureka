package vulkan

import game.main.OzConstants.ApplicationName
import game.main.OzConstants.ApplicationVersion
import game.main.OzConstants.InstanceExtensions
import game.main.OzConstants.Layers
import game.main.OzConstants.VulkanAPIVersion
import game.main.OzConstants.debug
import mu.KotlinLogging
import org.lwjgl.vulkan.EXTDebugUtils
import uno.glfw.glfw
import uno.requiredInstanceExtensions
import vkk.identifiers.Instance
import vkk.vk
import vkk.vk10.enumerateInstanceExtensionProperties
import vkk.vk10.instanceLayerProperties
import vkk.vk10.physicalDevices
import vkk.vk10.properties
import vkk.vk10.structs.ApplicationInfo
import vkk.vk10.structs.InstanceCreateInfo

/**
 * Created by CowardlyLion on 2020/4/20 14:16
 */
class OzInstance(val ozVulkan: OzVulkan){

    val logger = KotlinLogging.logger {  }

    val applicationInfo = ApplicationInfo(ApplicationName, ApplicationVersion, apiVersion = VulkanAPIVersion)
    val instanceCI = InstanceCreateInfo(applicationInfo, Layers, InstanceExtensions)
    val instance = Instance(instanceCI)

    val physicalDevices = instance.physicalDevices


    init {
        ozVulkan.cleanups.addNode(this::destroy)
    }

    fun destroy() {
        instance.destroy()
    }

    fun printPhysicalDevices() {
        physicalDevices.forEach {
            logger.info { it.properties.deviceName }
        }
    }

}