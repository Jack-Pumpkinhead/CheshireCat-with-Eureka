package land.oz.turorial

import mu.KotlinLogging
import org.lwjgl.system.Checks
import org.lwjgl.vulkan.EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME
import org.lwjgl.vulkan.KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME
import org.lwjgl.vulkan.VK10.*
import uno.createSurface
import uno.glfw.GlfwWindow
import uno.glfw.glfw
import uno.glfw.windowHint.Api.*
import uno.requiredInstanceExtensions
import vkk.*
import vkk.entities.*
import vkk.extensions.*
import vkk.identifiers.Instance
import vkk.identifiers.PhysicalDevice
import vkk.identifiers.Queue
import vkk.vk10.*
import vkk.vk10.structs.*
import java.util.function.Consumer
import kotlin.random.Random

/**
 * Created by CowardlyLion on 2020/4/9 14:16
 */

fun main() {
    HelloTriangle()
}

class HelloTriangle {

    val logger = KotlinLogging.logger {}

    object struct_window {
        val window: GlfwWindow
        val width = 937
        val height = 531
        val title = "HelloTriangleVulkan!"

        init {
            glfw.init()
            glfw.windowHint {
                api = None
                resizable = false
            }
            window = GlfwWindow(width, height, title)
            window.installDefaultCallbacks()
        }
    }

    init {
        struct_window
        initVulkan()
        mainLoop()
        cleanup()
    }


    private fun initVulkan() {
        struct_instance
        setupDebugMessenger()
        struct_surface
        struct_physicaldevice
        struct_LogicalDevice
        struct_swapchain
        struct_imageview
    }

    object struct_instance {
        val logger = KotlinLogging.logger {}
        val applicationInfo: ApplicationInfo = ApplicationInfo(
            applicationName = "Hello Triangle",
            applicationVersion = VK_MAKE_VERSION(1, 0, 0),
            apiVersion = VK_API_VERSION_1_0
        )
        val enabledLayerNames: List<String> = listOf(
//            "VK_LAYER_LUNARG_standard_validation",
//            "VK_LAYER_NV_optimus",
//            "VK_LAYER_VALVE_steam_overlay"
        )
        val enabledExtensionNames: ArrayList<String> = glfw.requiredInstanceExtensions
        init {
            if (Checks.DEBUG) {
                enabledExtensionNames += VK_EXT_DEBUG_UTILS_EXTENSION_NAME
            }
        }
        val createInfo: InstanceCreateInfo = InstanceCreateInfo(applicationInfo, enabledLayerNames, enabledExtensionNames)

        val instance: Instance = Instance(createInfo)

        init {
            assert(instance.isValid)
        }

        private fun checkInstanceExt() {
            logger.info("all exts:")
            vk.enumerateInstanceExtensionProperties().forEach { logger.info("\t${it.extensionName}") }
            logger.info("enabled exts:")
            enabledExtensionNames.forEach{ logger.info("\t$it")}
        }

        private fun checkValidationLayerSupport() {
            logger.info("all layer:")
            vk.instanceLayerProperties.forEach { logger.info("\t${it.layerName}") }
            logger.info("enabled layer:")
            enabledLayerNames.forEach { logger.info("\t$it") }
        }

    }

    private fun setupDebugMessenger() {
        if(!enableValidationLayers) return
//        VkDebugUtilsMessengerEXT_Array()
//        VkStructureType.DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT.
//        val createInfo= DebugReportCallbackCreateInfo(
//            flags = WARNING_BIT_EXT or PERFORMANCE_WARNING_BIT_EXT or ERROR_BIT_EXT or DEBUG_BIT_EXT
//        )
        /*val createInfo = VkDebugUtilsMessengerCreateInfoEXT.mallocStack()
            .sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
            .messageSeverity(
                VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT or
                        VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT or
                        VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT
            ).messageType(
                VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT or
                        VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT or
                        VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT
            ).pfnUserCallback(){ messageSeverity, messageTypes, pCallbackData, pUserData ->
                println("validation layer: ${VkDebugUtilsMessengerCallbackDataEXT.npMessageString(pCallbackData)}")
                return@pfnUserCallback VK_FALSE
            }*/
//        val debugM = instance.createDebugReportCallbackEXT(createInfo)

//        val debugMessenger = instance.createDebugReportCallbackEXT(
//            createInfo, null, longArrayOf()
//        )
//        debug? 不存在的
//        DebugUtilsMessengerCallbackDataEXT
        val callback: DebugUtilsMessengerCallbackEXT = { messageSeverity, messageTypes, callbackData, userData ->
            false
        }


    }
    object struct_surface {

        val logger = KotlinLogging.logger {}
        val surface: VkSurfaceKHR = struct_instance.instance.createSurface(struct_window.window)

        init {
            if (surface.isInvalid) {
                logger.error("surface invalid!")
            }
        }
    }
    object struct_physicaldevice {
        val logger = KotlinLogging.logger {}
        val physicalDevices: Array<PhysicalDevice> = struct_instance.instance.physicalDevices
        val physicalDevice_wrapper = physicalDevices.asSequence().map(::PhysicalDeviceFilter).first {
            logger.info("physicalDeviceName: ${it.name}")
            it.good()
        }


        val physicalDevice: PhysicalDevice = physicalDevice_wrapper.pd
    }
    object struct_LogicalDevice {
        val deviceQueueCreateInfo_graphics = DeviceQueueCreateInfo(
            queueFamilyIndex = struct_physicaldevice.physicalDevice_wrapper.surfaceSwapChainSupport.queuefamily_graphic,
            queuePriorities = floatArrayOf(Random.nextFloat())
        )
        val deviceQueueCreateInfo_presentation = DeviceQueueCreateInfo(
            queueFamilyIndex = struct_physicaldevice.physicalDevice_wrapper.surfaceSwapChainSupport.queuefamily_present,
            queuePriorities = floatArrayOf(Random.nextFloat())
        )
        val deviceCreateInfo = DeviceCreateInfo(
            queueCreateInfos = listOf(deviceQueueCreateInfo_graphics, deviceQueueCreateInfo_presentation),
            enabledExtensionNames = struct_physicaldevice.physicalDevice_wrapper.physicalDeviceRequiredExts,
            enabledFeatures = struct_physicaldevice.physicalDevice.features
        )
        val device = struct_physicaldevice.physicalDevice.createDevice(deviceCreateInfo)
        val graphicsQueue : Queue = device.getQueue(deviceQueueCreateInfo_graphics.queueFamilyIndex)
        val presentQueue: Queue = device.getQueue(deviceQueueCreateInfo_presentation.queueFamilyIndex)
    }

    object struct_swapchain {
        val sc = struct_physicaldevice.physicalDevice_wrapper.surfaceSwapChainSupport
        val format = chooseSwapSurfaceFormat(sc.formats)
        val present = chooseSwapPresentMode(sc.presentModes)
        val extent = chooseSwapExtent(sc.capabilities)

        var imageCount =
            (sc.capabilities.minImageCount + 1).coerceAtMost(
                sc.capabilities.maxImageCount
            )
        val swapchainCreateInfoKHR: SwapchainCreateInfoKHR = SwapchainCreateInfoKHR(
            surface = struct_surface.surface,
            minImageCount = imageCount,
            imageFormat = format.format,
            imageColorSpace = format.colorSpace,
            imageExtent = extent,
            imageArrayLayers = 1,
            imageUsage = VkImageUsage.COLOR_ATTACHMENT_BIT.i,
            clipped = true,
            compositeAlpha = VkCompositeAlphaKHR.OPAQUE_BIT,
            imageSharingMode = VkSharingMode.EXCLUSIVE,
            preTransform = sc.capabilities.currentTransform,
            presentMode = present
        )

        init {
            if (sc.queuefamily_diff()) {
                swapchainCreateInfoKHR.imageSharingMode = VkSharingMode.CONCURRENT
                swapchainCreateInfoKHR.queueFamilyIndices = intArrayOf(
                    sc.queuefamily_graphic,
                    sc.queuefamily_present
                )
            }
        }

        val swapchain: VkSwapchainKHR = struct_LogicalDevice.device.createSwapchainKHR(swapchainCreateInfoKHR)

        val swapChainImages: VkImage_Array = struct_LogicalDevice.device.getSwapchainImagesKHR(swapchain)
        fun chooseSwapSurfaceFormat(surfaceFormats: List<SurfaceFormatKHR>): SurfaceFormatKHR =
            surfaceFormats.firstOrNull {
                it.format == VkFormat.B8G8R8A8_SRGB && it.colorSpace == VkColorSpaceKHR.SRGB_NONLINEAR_KHR
            } ?: surfaceFormats[0]

        fun chooseSwapPresentMode(presentmodes: VkPresentModeKHR_Array): VkPresentModeKHR =
            if (presentmodes.array.contains(VkPresentModeKHR.MAILBOX.i)) VkPresentModeKHR.MAILBOX
            else VkPresentModeKHR.FIFO
        fun chooseSwapExtent(capabilitiesKHR: SurfaceCapabilitiesKHR): Extent2D {
            return if (capabilitiesKHR.currentExtent.width != Int.MAX_VALUE) {
                capabilitiesKHR.currentExtent
            } else {
                Extent2D(
                    struct_window.width.coerceIn(capabilitiesKHR.minImageExtent.width, capabilitiesKHR.maxImageExtent.width),
                    struct_window.height.coerceIn(capabilitiesKHR.minImageExtent.height, capabilitiesKHR.maxImageExtent.height)
                )
            }
        }
    }
    object struct_imageview {


        val imageViewCreateInfo = ImageViewCreateInfo(
            viewType = VkImageViewType._2D,
            format = struct_swapchain.swapchainCreateInfoKHR.imageFormat,
            subresourceRange = ImageSubresourceRange(
                aspectMask = VkImageAspect.COLOR_BIT.i,
                baseMipLevel = 0,
                levelCount = 1,
                baseArrayLayer = 0,
                layerCount = 1
            )
        )
        val swapchainImageViews: VkImageView_Array = struct_LogicalDevice.device.createImageViewArray(
            imageViewCreateInfo,
            images = struct_swapchain.swapChainImages
        )
    }
    val enableValidationLayers = Checks.DEBUG

    fun mainLoop() {
        //autoswap implied
        struct_window.window.loop(Consumer {
//            logger.info("a")
        })
    }

    private fun cleanup() {
        struct_imageview.swapchainImageViews.indices.forEach { struct_LogicalDevice.device.destroy(struct_imageview.swapchainImageViews[it]) }
//        swapChainImageViews.forEach { device.destroy(it) }
        struct_LogicalDevice.device.destroy(struct_swapchain.swapchain)
        struct_LogicalDevice.device.destroy()
        struct_instance.instance.destroy(struct_surface.surface)
        struct_instance.instance.destroy()
        struct_window.window.destroy()
        glfw.terminate()
    }

    // No correct layer yet.
    // "Also ensure that your SDK version is at least 1.1.106.0 to support the VK_LAYER_KHRONOS_validation layer."
    class PhysicalDeviceFilter(val pd: PhysicalDevice) {
        val name = pd.properties.deviceName


        val type = pd.properties.deviceType
        val supportGeometryShader = pd.features.geometryShader
        val exts = pd.enumerateDeviceExtensionProperties()

        val extNames = exts.map(ExtensionProperties::extensionName)

        val surfaceSwapChainSupport = SurfaceSwapChainSupport(pd, struct_surface.surface)

        fun good(): Boolean =
            type == VkPhysicalDeviceType.DISCRETE_GPU &&
                    supportGeometryShader &&
                    extNames.containsAll(physicalDeviceRequiredExts) &&
                    surfaceSwapChainSupport.notEmpty()
        val physicalDeviceRequiredExts = setOf(VK_KHR_SWAPCHAIN_EXTENSION_NAME)
        val logger = KotlinLogging.logger {}

        fun printExtNames() {
            logger.info("exts of physical device: $name")
            extNames.forEach { logger.info("\t$it") }
        }
    }

    class SurfaceSwapChainSupport(physicalDevice: PhysicalDevice, surface: VkSurfaceKHR) {

        val capabilities: SurfaceCapabilitiesKHR = physicalDevice getSurfaceCapabilitiesKHR surface

        val formats: ArrayList<SurfaceFormatKHR> = physicalDevice getSurfaceFormatsKHR      surface

        val presentModes: VkPresentModeKHR_Array = physicalDevice getSurfacePresentModesKHR surface
        val queuefamily_graphic = physicalDevice.queueFamilyProperties.indexOfFirst { it.queueFlags has VkQueueFlag.GRAPHICS_BIT }
        val queuefamily_present = physicalDevice.queueFamilyProperties.indices.indexOfFirst { physicalDevice.getSurfaceSupportKHR(it, surface) }
        fun notEmpty() =
            formats.isNotEmpty() && presentModes.size > 0 && queuefamily_graphic != -1 && queuefamily_present != -1
        fun queuefamily_diff() = queuefamily_graphic != queuefamily_present
    }

}
