package land.oz.turorial

import mu.KotlinLogging
import org.lwjgl.system.Checks
import org.lwjgl.vulkan.EXTDebugUtils.VK_EXT_DEBUG_UTILS_EXTENSION_NAME
import org.lwjgl.vulkan.VK10.VK_API_VERSION_1_0
import org.lwjgl.vulkan.VK10.VK_MAKE_VERSION
import uno.glfw.GlfwWindow
import uno.glfw.glfw
import uno.glfw.windowHint.Api.*
import uno.requiredInstanceExtensions
import vkk.VkPhysicalDeviceType
import vkk.VkQueueFlag
import vkk.extensions.DebugUtilsMessengerCallbackEXT
import vkk.has
import vkk.identifiers.Device
import vkk.identifiers.Instance
import vkk.identifiers.PhysicalDevice
import vkk.identifiers.Queue
import vkk.vk
import vkk.vk10.*
import vkk.vk10.structs.ApplicationInfo
import vkk.vk10.structs.DeviceCreateInfo
import vkk.vk10.structs.DeviceQueueCreateInfo
import vkk.vk10.structs.InstanceCreateInfo
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
        pickPhysicalDevice()
        createLogicalDevice()
    }

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
                && findQueueFamilies(physicalDevice) != -1
    }

    var queueFamilyIndex = -1
    //return index
    fun findQueueFamilies(physicalDevice: PhysicalDevice): Int {
        var find = -1
        for ((i, queueFamily) in physicalDevice.queueFamilyProperties.withIndex()) {
            logger.info("queueFamily support Graphics_bit")
            logger.info("${queueFamily.queueFlags}")
            if (queueFamily.queueFlags has VkQueueFlag.GRAPHICS_BIT) {
                logger.info("+1")
                find = i
            }
        }
        queueFamilyIndex = find
        return find
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

    val validationLayers: List<String>
        get() = listOf("VK_LAYER_LUNARG_standard_validation")

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

        checkExt()
        println()
        checkValidationLayerSupport()


    }

    lateinit var device: Device
    lateinit var queue: Queue

    fun createLogicalDevice() {
        val deviceQueueCreateInfo = DeviceQueueCreateInfo(
            queueFamilyIndex = queueFamilyIndex,
            queuePriorities = floatArrayOf(Random.nextFloat())
        )
        val deviceCreateInfo = DeviceCreateInfo(
            queueCreateInfo = deviceQueueCreateInfo,
            enabledFeatures = physicalDevice.features
        )
        logger.info("phDevExt")
        physicalDevice.enumerateDeviceExtensionProperties().forEach {
            logger.info("${it.extensionName}")
        }
        device = physicalDevice.createDevice(deviceCreateInfo)
        queue = device.getQueue(queueFamilyIndex)
    }

    val enableValidationLayers = Checks.DEBUG

    fun getRequiredExtensions(): ArrayList<String> {

        val ext = glfw.requiredInstanceExtensions
        if (enableValidationLayers) {
            ext += VK_EXT_DEBUG_UTILS_EXTENSION_NAME
        }
        return ext
    }

    private fun checkExt() {
        //required vs supported
        glfw.requiredInstanceExtensions.forEach(::println)

        println()

        val exts = vk.enumerateInstanceExtensionProperties(null)
        exts.forEach { println(it.extensionName) }
    }
    //TODO: check layor contain in this
    fun checkValidationLayerSupport() {
        val layers = vk.instanceLayerProperties
        layers.forEach { println(it.layerName) }
        println()
        validationLayers.forEach { println()}
    }




    private fun cleanup() {
        device.destroy()
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

}