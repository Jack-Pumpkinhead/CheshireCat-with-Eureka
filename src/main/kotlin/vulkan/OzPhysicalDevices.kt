package vulkan

import vkk.vk10.physicalDevices
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/5/8 20:28
 */
class OzPhysicalDevices(val instance: OzInstance, val surface: OzSurface) {


    val list = instance.physicalDevices.map(::OzPhysicalDevice)
    val ssList = list.map { SurfaceSwapchainSupport(it, surface.surface) }

    val physicalDevice: OzPhysicalDevice
    val surfaceSupport: SurfaceSwapchainSupport

    init {
        val firstSuit = list.zip(ssList).indexOfFirst { (pd, ss) ->
            pd.supported() && ss.supported()
        }
        if (firstSuit != -1) {
            physicalDevice = list[firstSuit]
            surfaceSupport = ssList[firstSuit]
        } else {
            OzVulkan.logger.info {
                "no suitable physical device!"
            }
            physicalDevice = list[0]
            surfaceSupport = ssList[0]
        }
    }

}