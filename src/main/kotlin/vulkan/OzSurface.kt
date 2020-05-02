package vulkan

import game.window.OzWindow
import mu.KotlinLogging
import uno.createSurface
import vkk.entities.VkSurfaceKHR
import vkk.extensions.destroy

/**
 * Created by CowardlyLion on 2020/4/20 18:31
 */
class OzSurface(val ozVulkan: OzVulkan, val ozInstance: OzInstance, val ozWindow: OzWindow) {

    val logger = KotlinLogging.logger { }

    val surface: VkSurfaceKHR = ozInstance.instance.createSurface(ozWindow)

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(ozInstance::destroy, this::destroy)
    }

    fun destroy() {
        ozInstance.instance.destroy(surface)
    }
}