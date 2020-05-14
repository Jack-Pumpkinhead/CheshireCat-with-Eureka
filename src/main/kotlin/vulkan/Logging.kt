package vulkan

import mu.KLogger
import vkk.VkQueueFlag
import vkk.extensions.getSurfaceSupportKHR
import vkk.has
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/5/8 20:38
 */

fun SurfaceSwapchainSupport.logging(logger: KLogger) {
    logger.info {
        "imageCount: $imageCount"
    }
    logger.info {
        "surfaceSwapchainSupport: pd: ${physicalDevice.pd.address()}"
    }
    logger.info {
        "format: ${surfaceFormat.format.i}\t colorspace: ${surfaceFormat.colorSpace.i}\t present mode: ${presentMode.i}"
    }
    logger.info {
        "graphic: $queuefamily_graphic\t" +
                "present: $queuefamily_present\t" +
                "transfer: $queuefamily_transfer"
    }
    physicalDevice.queueFamilyProperties.forEachIndexed { index, queueFamilyProperties ->
        logger.info {
            "queueFamily $index :\t" +
                    "count: ${queueFamilyProperties.queueCount}\t" +
                    "graphic bit: ${queueFamilyProperties.queueFlags has VkQueueFlag.GRAPHICS_BIT}\t" +
                    "transfer bit: ${queueFamilyProperties.queueFlags has VkQueueFlag.TRANSFER_BIT}\t" +
                    "protected bit: ${queueFamilyProperties.queueFlags has VkQueueFlag.PROTECTED_BIT}\t" +
                    "surface support: ${physicalDevice.pd.getSurfaceSupportKHR(index, surface)}\t"
        }
    }
}