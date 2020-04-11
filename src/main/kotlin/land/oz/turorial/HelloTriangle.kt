package land.oz.turorial

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.Checks
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.util.vma.VmaVulkanFunctions
import org.lwjgl.vulkan.EXTDebugUtils.*
import org.lwjgl.vulkan.VK10.*
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

//import kool.pointers

/**
 * Created by CowardlyLion on 2020/4/9 14:16
 */

fun main() {
    HelloTriangle()
}

class HelloTriangle {

    var window: Long = 0

    private val width = 937

    private val height = 531

    private fun initWindow() {
        if(!glfwInit()) error("glfwInit() failed")
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE)
        window = glfwCreateWindow(width, height, "Vulkan", NULL, NULL)
        assert(window != NULL)
    }


    private fun initVulkan() {
        create_instance()
        setupDebugMessenger()
        pickPhysicalDevice()
    }

//    lateinit var physicalDevice: PhysicalDevice
    private fun pickPhysicalDevice() {
        println("aaa")
        val devices = instance.physicalDevices
        println("bbb")

    for (device in devices) {
//        MemoryStack.stackPush().use {
//                        device.properties
//                        println("aaa")
//                    }
//        val stackPush = MemoryStack.stackPush()
//        PhysicalDeviceProperties(kool.pointers.BytePtr(VmaVulkanFunctions.nvkGetPhysicalDeviceProperties(device.address())))
//        println(device.properties.deviceType)
//        MemoryStack.stackPop()
//        println("ccc")
//        println("phyDevPro: ${VmaVulkanFunctions.nvkGetPhysicalDeviceProperties(device.address())}")

//        println(device.instance)
//        println(device.capabilities.VK_EXT_acquire_xlib_display)
//        println(device.toString())
//        println(device.capabilities.vkGetPhysicalDeviceMemoryProperties)
//        println(device.capabilities.vkGetPhysicalDeviceProperties)
//        println(device.capabilities.vkGetPhysicalDeviceProperties2)
//        println(device.capabilities.vkGetPhysicalDeviceProperties2KHR)
//        println(device.capabilities.VK_KHR_get_physical_device_properties2)
//        println(device.capabilities.Vulkan10)
//        println(device.capabilities.Vulkan11)
        println("deviceName: ${device.properties.deviceName}")
//            println(device.properties.deviceType)
//            println("${device.properties.deviceName} valid: ${device.isValid}")
        }
//        physicalDevice = devices[0]
//        physicalDevice.properties.deviceType
//        physicalDevice.features
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


    private fun mainLoop() {
        while (!glfwWindowShouldClose(window)) {
            glfwPollEvents()
        }
    }

    private fun cleanup() {
        instance.destroy()
        glfwDestroyWindow(window)
        glfwTerminate()
    }

    init {
        initWindow()
        initVulkan()
        mainLoop()
        cleanup()
    }
}