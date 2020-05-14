package vulkan

import game.window.OzWindow
import mu.KotlinLogging
import uno.createSurface
import vkk.entities.VkSurfaceKHR
import vkk.extensions.destroy

/**
 * Created by CowardlyLion on 2020/4/20 18:31
 */
class OzSurface(val ozInstance: OzInstance, val ozWindow: OzWindow) {

    val surface: VkSurfaceKHR = ozInstance.instance.createSurface(ozWindow)

    fun destroy() {
        ozInstance.instance.destroy(surface)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }
}