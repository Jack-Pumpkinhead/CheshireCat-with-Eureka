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
import vkk.identifiers.Device
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

    lateinit var window: GlfwWindow

    private val width = 937

    private val height = 531

    private fun initWindow() {
        glfw.init()
        glfw.windowHint {
            api = None
            resizable = false
        }
        window = GlfwWindow(
            width = width,
            height = height,
            title = "HelloTriangleVulkan!"
        )
        window.installDefaultCallbacks()
    }


    private fun initVulkan() {
        create_instance()
        setupDebugMessenger()
        createSurface()
        pickPhysicalDevice()
        createLogicalDevice()
        createSwapChain()
    }



    private fun createSwapChain() {
//        querySwapChainSupport(physicalDevice)
        val format = chooseSwapSurfaceFormat(SurfaceSwapChainSupport.surfaceFormatsKHR)
        val present = chooseSwapPresentMode(SurfaceSwapChainSupport.surfacePresentModesKHR)
        val extent = chooseSwapExtent(SurfaceSwapChainSupport.surfaceCapabilitiesKHR)
        var imageCount = SurfaceSwapChainSupport.surfaceCapabilitiesKHR.minImageCount + 1
        if (SurfaceSwapChainSupport.surfaceCapabilitiesKHR.maxImageCount in 1 until imageCount) {
            imageCount = SurfaceSwapChainSupport.surfaceCapabilitiesKHR.maxImageCount
        }
        val difIndices = QueueFamilyIndices.graphicsFamily != QueueFamilyIndices.presentFamily
        swapchainCreateInfoKHR = SwapchainCreateInfoKHR(
            surface = surface,
            minImageCount = imageCount,
            imageFormat = format.format,
            imageColorSpace = format.colorSpace,
            imageExtent = extent,
            imageArrayLayers = 1,
            imageUsage = VkImageUsage.COLOR_ATTACHMENT_BIT.i,
            clipped = true,
            compositeAlpha = VkCompositeAlphaKHR.OPAQUE_BIT,
            imageSharingMode = if (difIndices) VkSharingMode.CONCURRENT else VkSharingMode.EXCLUSIVE,
            queueFamilyIndices = if (difIndices) intArrayOf(
                QueueFamilyIndices.graphicsFamily,
                QueueFamilyIndices.presentFamily
            ) else null,
            preTransform = SurfaceSwapChainSupport.surfaceCapabilitiesKHR.currentTransform,
            presentMode = present
        )
        swapchain = device.createSwapchainKHR(swapchainCreateInfoKHR)

        swapChainImages = device.getSwapchainImagesKHR(swapchain)
        logger.info("swapChain ok")
    }
    lateinit var swapchainCreateInfoKHR: SwapchainCreateInfoKHR
    var swapChainImages: VkImage_Array = VkImage_Array()

    var swapchain: VkSwapchainKHR = VkSwapchainKHR.NULL
    lateinit var physicalDevice: PhysicalDevice

    private fun pickPhysicalDevice() {
        val devices = instance.physicalDevices
        for (device in devices) {
            logger.info("deviceName: ${device.properties.deviceName}")
        }
        physicalDevice = devices[0] //choose correct one
        assert(isDeviceSuitable(physicalDevice))
    }


    fun isDeviceSuitable(physicalDevice: PhysicalDevice): Boolean {

        return physicalDevice.properties.deviceType == VkPhysicalDeviceType.DISCRETE_GPU
                && physicalDevice.features.geometryShader
                && findQueueFamilies(physicalDevice)
                && checkDeviceExt(physicalDevice)
                && querySwapChainSupport(physicalDevice)
    }
    val physicalDeviceRequiredExts = setOf(VK_KHR_SWAPCHAIN_EXTENSION_NAME)

    private fun checkDeviceExt(physicalDevice: PhysicalDevice): Boolean {
        val ext = physicalDevice.enumerateDeviceExtensionProperties().
                    mapTo(mutableSetOf(), ExtensionProperties::extensionName)
        return physicalDeviceRequiredExts.all { ext.contains(it) }
    }
    object QueueFamilyIndices {
        var graphicsFamily = -1
        var presentFamily = -1
        fun allFind(): Boolean {
            return graphicsFamily != -1 &&
                    presentFamily != -1
        }

    }

    //simply find the last one (of family) suit
    fun findQueueFamilies(physicalDevice: PhysicalDevice): Boolean {

        for ((i, queueFamily) in physicalDevice.queueFamilyProperties.withIndex()) {
            if (queueFamily.queueFlags has VkQueueFlag.GRAPHICS_BIT) {
                logger.info("queueFamily $i support Graphics_bit")
                QueueFamilyIndices.graphicsFamily = i
            }

            if (physicalDevice.getSurfaceSupportKHR(i,surface)) {
                logger.info("queueFamily $i support SurfaceSupport")
                QueueFamilyIndices.presentFamily = i
            }
        }
        return QueueFamilyIndices.allFind()
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

    lateinit var instance: Instance

    // No correct layer yet.
    // "Also ensure that your SDK version is at least 1.1.106.0 to support the VK_LAYER_KHRONOS_validation layer."
    val validationLayers: List<String>
        get() = listOf(
            "VK_LAYER_LUNARG_standard_validation",
            "VK_LAYER_NV_optimus",
            "VK_LAYER_VALVE_steam_overlay"
        )

    private fun create_instance() {

        val appInfo = ApplicationInfo(
            applicationName = "Hello Triangle",
            applicationVersion = VK_MAKE_VERSION(1, 0, 0),
            apiVersion = VK_API_VERSION_1_0
        )
        val createInfo = InstanceCreateInfo(
            applicationInfo = appInfo,
            enabledExtensionNames = getRequiredExtensions(),
            enabledLayerNames = validationLayers
        )

        instance = Instance(createInfo)
//        vkinstance= VkInstance(instance.address(),instance.createInfo)
        assert(instance.isValid)

        checkInstanceExt()
        println()
        checkValidationLayerSupport()


    }

    lateinit var device: Device
    object Queues {
        lateinit var graphicsQueue : Queue
        lateinit var presentQueue: Queue

    }

    fun createLogicalDevice() {
        val deviceQueueCreateInfo_graphics = DeviceQueueCreateInfo(
            queueFamilyIndex = QueueFamilyIndices.graphicsFamily,
            queuePriorities = floatArrayOf(Random.nextFloat())
        )
        val deviceQueueCreateInfo_presentation = DeviceQueueCreateInfo(
            queueFamilyIndex = QueueFamilyIndices.presentFamily,
            queuePriorities = floatArrayOf(Random.nextFloat())
        )
        val deviceCreateInfo = DeviceCreateInfo(
            queueCreateInfos = listOf(deviceQueueCreateInfo_graphics, deviceQueueCreateInfo_presentation),
            enabledExtensionNames = physicalDeviceRequiredExts,
            enabledFeatures = physicalDevice.features
        )
        logger.info("phDevExt")
        physicalDevice.enumerateDeviceExtensionProperties().forEach {
            logger.info("${it.extensionName}")
        }
//        logger.info("phyDevFeature")
//        physicalDevice.features.
        device = physicalDevice.createDevice(deviceCreateInfo)
        Queues.graphicsQueue = device.getQueue(QueueFamilyIndices.graphicsFamily)
        Queues.presentQueue = device.getQueue(QueueFamilyIndices.presentFamily)
    }

    val enableValidationLayers = Checks.DEBUG

    fun getRequiredExtensions(): ArrayList<String> {

        val ext = glfw.requiredInstanceExtensions
        if (enableValidationLayers) {
            ext += VK_EXT_DEBUG_UTILS_EXTENSION_NAME
        }
        return ext
    }
    private fun checkInstanceExt() {
        //required vs supported
        glfw.requiredInstanceExtensions.forEach(::println)

        println()

        val exts = vk.enumerateInstanceExtensionProperties(null)
        exts.forEach { println(it.extensionName) }
    }




    //TODO: check layer contain in this
    fun checkValidationLayerSupport() {
        val layers = vk.instanceLayerProperties
        println("all layer:")
        layers.forEach { println(it.layerName) }
        println("should support layer:")
        validationLayers.forEach { println(it)}
    }


    private fun cleanup() {
        device.destroy(swapchain)
        device.destroy()
        if (surface == VkSurfaceKHR.NULL) {
            logger.debug("surface is NULL")
        }
        instance.destroy(surface)
        instance.destroy()
        window.destroy()
        glfw.terminate()
    }
    init {
        initWindow()
        initVulkan()
        window.loop(Consumer {
//            logger.info("a")
        })
        cleanup()
    }
    var surface : VkSurfaceKHR = VkSurfaceKHR.NULL

    fun createSurface() {
//        VkWin32SurfaceCreateFlagsKHR
//        window.hwnd
        surface = instance.createSurface(window)
    }
    object SurfaceSwapChainSupport {
        lateinit var surfaceCapabilitiesKHR: SurfaceCapabilitiesKHR
        lateinit var surfaceFormatsKHR: ArrayList<SurfaceFormatKHR>
        var surfacePresentModesKHR: VkPresentModeKHR_Array = VkPresentModeKHR_Array()
        fun notEmpty() = surfaceFormatsKHR.isNotEmpty() && surfacePresentModesKHR.size > 0

    }

    fun querySwapChainSupport(physicalDevice: PhysicalDevice): Boolean {
        SurfaceSwapChainSupport.surfaceCapabilitiesKHR = physicalDevice.getSurfaceCapabilitiesKHR(surface)
        SurfaceSwapChainSupport.surfaceFormatsKHR = physicalDevice.getSurfaceFormatsKHR(surface)
        SurfaceSwapChainSupport.surfacePresentModesKHR = physicalDevice.getSurfacePresentModesKHR(surface)
        return SurfaceSwapChainSupport.notEmpty()
    }

    fun chooseSwapSurfaceFormat(surfaceFormats: List<SurfaceFormatKHR>): SurfaceFormatKHR {
        for (sFormat in surfaceFormats) {
            if (sFormat.format == VkFormat.B8G8R8A8_SRGB && sFormat.colorSpace == VkColorSpaceKHR.SRGB_NONLINEAR_KHR) {
                return sFormat
            }
        }
//        throw IllegalStateException()
        return surfaceFormats[0]
    }

    fun chooseSwapPresentMode(presentmodes: VkPresentModeKHR_Array): VkPresentModeKHR {
        for (i in presentmodes.indices) {
            if (presentmodes[i] == VkPresentModeKHR.MAILBOX) {
                return presentmodes[i]
            }
        }
        return VkPresentModeKHR.FIFO
    }

    fun chooseSwapExtent(capabilitiesKHR: SurfaceCapabilitiesKHR): Extent2D {
        return if (capabilitiesKHR.currentExtent.width != Int.MAX_VALUE) {
            capabilitiesKHR.currentExtent
        } else {
            Extent2D(
                width.coerceIn(capabilitiesKHR.minImageExtent.width, capabilitiesKHR.maxImageExtent.width),
                height.coerceIn(capabilitiesKHR.minImageExtent.height, capabilitiesKHR.maxImageExtent.height)
            )
        }
    }
}