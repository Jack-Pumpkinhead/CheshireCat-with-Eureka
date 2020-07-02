package game.main

import glm_.detail.GLM_DEPTH_CLIP_SPACE
import glm_.detail.GlmDepthClipSpace
import mu.KotlinLogging
import org.lwjgl.system.Checks
import org.lwjgl.vulkan.EXTDebugUtils
import org.lwjgl.vulkan.KHRSwapchain
import org.lwjgl.vulkan.VK10
import uno.glfw.glfw
import uno.requiredInstanceExtensions
import vkk.vk
import vkk.vk10.enumerateInstanceExtensionProperties
import vkk.vk10.instanceLayerProperties
import vkk.vk10.structs.ExtensionProperties


/**
 * Created by CowardlyLion on 2020/4/20 12:47
 */

object OzConstants {
    const val WIDTH = 973
    const val HEIGHT = 531
    const val TITLE = "Eureka"
    val debug = Checks.DEBUG
    const val ApplicationName = "Eureka"
    val ApplicationVersion = VK10.VK_MAKE_VERSION(1, 0, 0)
    val VulkanAPIVersion = VK10.VK_API_VERSION_1_0
    val Extensions = setOf(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME)

    val Layers: List<String> = listOf(
//            "VK_LAYER_LUNARG_standard_validation",
//            "VK_LAYER_NV_optimus",
//            "VK_LAYER_VALVE_steam_overlay"
    )

    val InstanceExtensions: ArrayList<String> = glfw.requiredInstanceExtensions

    val OzDefaultName = "Oz-Default"

    init {
//        vkk.DEBUG = false
        vkk.DEBUG = debug
        if (debug) {
            InstanceExtensions += EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME
        }

        GLM_DEPTH_CLIP_SPACE = GlmDepthClipSpace.ZERO_TO_ONE
    }

}