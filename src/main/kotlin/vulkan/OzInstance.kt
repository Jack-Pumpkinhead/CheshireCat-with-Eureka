package vulkan

import game.main.OzConstants.ApplicationName
import game.main.OzConstants.ApplicationVersion
import game.main.OzConstants.InstanceExtensions
import game.main.OzConstants.Layers
import game.main.OzConstants.VulkanAPIVersion
import mu.KotlinLogging
import vkk.identifiers.Instance
import vkk.vk10.physicalDevices
import vkk.vk10.properties
import vkk.vk10.structs.ApplicationInfo
import vkk.vk10.structs.InstanceCreateInfo

/**
 * Created by CowardlyLion on 2020/4/20 14:16
 */
class OzInstance(){

    val applicationInfo = ApplicationInfo(ApplicationName, ApplicationVersion, apiVersion = VulkanAPIVersion)
    val instanceCI = InstanceCreateInfo(applicationInfo, Layers, InstanceExtensions)
    val instance = Instance(instanceCI)

    val physicalDevices = instance.physicalDevices


    fun destroy() {
        instance.destroy()
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}