package land.oz.turorial

import glm_.vec4.Vec4
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
import vkk.identifiers.CommandBuffer
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
        struct_logicaldevice
        struct_swapchain
        struct_imageview
        struct_renderpass
        struct_graphicpipeline
        struct_framebuffer
        struct_commandpool
        struct_commandbuffers
        struct_semaphores
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
        if(!Checks.DEBUG) return
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
    object struct_logicaldevice {
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

        val swapchain: VkSwapchainKHR = struct_logicaldevice.device.createSwapchainKHR(swapchainCreateInfoKHR)

        val swapChainImages: VkImage_Array = struct_logicaldevice.device.getSwapchainImagesKHR(swapchain)
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
        val logger = KotlinLogging.logger { }

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
        val swapchainImageViews: VkImageView_Array = struct_logicaldevice.device.createImageViewArray(
            imageViewCreateInfo,
            images = struct_swapchain.swapChainImages
        )

        init {
            logger.info { "imageviews size: ${swapchainImageViews.size}" }
        }
    }
    object struct_renderpass {
        val renderPass: VkRenderPass

        init {
            val attachmentDescription = AttachmentDescription(
                format = struct_swapchain.format.format,
                samples = VkSampleCount._1_BIT,
                loadOp = VkAttachmentLoadOp.CLEAR,
                storeOp = VkAttachmentStoreOp.STORE,
                stencilLoadOp = VkAttachmentLoadOp.DONT_CARE,
                stencilStoreOp = VkAttachmentStoreOp.DONT_CARE,
                initialLayout = VkImageLayout.UNDEFINED,
                finalLayout = VkImageLayout.PRESENT_SRC_KHR
            )
            val attachmentReference = AttachmentReference(
                attachment = 0, //index of attachmentDescription array
                layout = VkImageLayout.COLOR_ATTACHMENT_OPTIMAL
            )

            val subpassDescription = SubpassDescription(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                colorAttachments = arrayOf(attachmentReference) //The index of the attachment in this array is directly referenced from the fragment shader with the layout(location = 0) out vec4 outColor directive!
            )

            val subpassDependency = SubpassDependency(
                srcSubpass = VK_SUBPASS_EXTERNAL,
                dstSubpass = 0,  //index of subpass in device
                srcStageMask = VkPipelineStage.COLOR_ATTACHMENT_OUTPUT_BIT.i,
                srcAccessMask = VkAccess(0).i,
                dstStageMask = VkPipelineStage.COLOR_ATTACHMENT_OUTPUT_BIT.i,
                dstAccessMask = VkAccess.COLOR_ATTACHMENT_WRITE_BIT.i
            )

            val renderPassCI = RenderPassCreateInfo(
                attachments = arrayOf(attachmentDescription),
                subpasses = arrayOf(subpassDescription),
                dependencies = arrayOf(subpassDependency)
            )

            renderPass = struct_logicaldevice.device.createRenderPass(renderPassCI)
            assert(renderPass.isValid)



        }

    }
    object struct_graphicpipeline {
        val logger=KotlinLogging.logger {}

        val pipelineLayout: VkPipelineLayout
        var graphicsPipelines: VkPipeline_Array = VkPipeline_Array()
        init {
            val glslToVulkan = GLSLToVulkan()
            val vert = ShaderModuleCreateInfo(code = glslToVulkan.vertex)
            val shadermodule_vert = struct_logicaldevice.device.createShaderModule(vert)
            val frag = ShaderModuleCreateInfo(code = glslToVulkan.fragment)
            val shadermodule_frag: VkShaderModule = struct_logicaldevice.device.createShaderModule(frag)

            logger.info { "shadermodule ${shadermodule_vert.isValid}" }
            assert(shadermodule_vert.isValid)
            assert(shadermodule_frag.isValid)

            val shaderstageCI_vert = PipelineShaderStageCreateInfo(
                stage = VkShaderStage.VERTEX_BIT,
                module = shadermodule_vert,
                name = "main"   //standard entry point
            )

            val shaderstageCI_frag = PipelineShaderStageCreateInfo(
                stage = VkShaderStage.FRAGMENT_BIT,
                module = shadermodule_frag,
                name = "main"
            )


            val vertexInputStateCI = PipelineVertexInputStateCreateInfo(
                vertexBindingDescriptions = null,
                vertexAttributeDescriptions = null
            )

            val inputAssemblyStateCI = PipelineInputAssemblyStateCreateInfo(
                topology = VkPrimitiveTopology.TRIANGLE_LIST,
                primitiveRestartEnable = false
            )
            val viewport = Viewport(
                x = 0.0f,
                y = 0.0f,
                width = struct_swapchain.extent.width.toFloat(),
                height = struct_swapchain.extent.height.toFloat(),
                minDepth = 0.0f,
                maxDepth = 1.0f
            )
            val scissor = Rect2D(
                offset = Offset2D(0, 0),
                extent = struct_swapchain.extent
            )

            val viewportStateCI = PipelineViewportStateCreateInfo(
                viewports = arrayOf(viewport),
                scissors = arrayOf(scissor)
            )

            val rasterizationSCI = PipelineRasterizationStateCreateInfo(
                depthClampEnable = false,
                rasterizerDiscardEnable = false,
                polygonMode = VkPolygonMode.FILL,
                lineWidth = 1.0f,
                cullMode = VkCullMode.BACK_BIT.i,
                frontFace = VkFrontFace.CLOCKWISE,
                depthBiasEnable = false,
                depthBiasConstantFactor = 0.0f,
                depthBiasClamp = 0.0f,
                depthBiasSlopeFactor = 0.0f
            )

            val multisampleSCI = PipelineMultisampleStateCreateInfo(
                sampleShadingEnable = false,
                rasterizationSamples = VkSampleCount._1_BIT,
                minSampleShading = 1.0f,
                sampleMask = null,
                alphaToCoverageEnable = false,
                alphaToOneEnable = false
            )
            val colorBlendAttachmentState = PipelineColorBlendAttachmentState(
                colorWriteMask = VkColorComponent.R_BIT or VkColorComponent.G_BIT or VkColorComponent.B_BIT or VkColorComponent.A_BIT,
                blendEnable = false,
                srcColorBlendFactor = VkBlendFactor.ONE,
                dstColorBlendFactor = VkBlendFactor.ZERO,
                colorBlendOp = VkBlendOp.ADD,
                srcAlphaBlendFactor = VkBlendFactor.ONE,
                dstAlphaBlendFactor = VkBlendFactor.ZERO,
                alphaBlendOp = VkBlendOp.ADD
            )
            val colorBlendSCI = PipelineColorBlendStateCreateInfo(
                logicOpEnable = false,
                logicOp = VkLogicOp.COPY,
                attachments = arrayOf(colorBlendAttachmentState),
                blendConstants = Vec4(0.0f, 0.0f, 0.0f, 0.0f)
            )
            val pipelineLayoutCI = PipelineLayoutCreateInfo(
                setLayouts = null,
                pushConstantRanges = null
            )
            pipelineLayout=struct_logicaldevice.device.createPipelineLayout(
                createInfo = pipelineLayoutCI
            )
            assert(pipelineLayout.isValid)


            val graphicsPipelineCI = GraphicsPipelineCreateInfo(
                stages = arrayOf(shaderstageCI_vert, shaderstageCI_frag),
                vertexInputState = vertexInputStateCI,
                inputAssemblyState = inputAssemblyStateCI,
                viewportState = viewportStateCI,
                rasterizationState = rasterizationSCI,
                multisampleState = multisampleSCI,
                depthStencilState = null,
                colorBlendState = colorBlendSCI,
                dynamicState = null,
                layout = pipelineLayout,
                renderPass = struct_renderpass.renderPass,
                subpass = 0,
                basePipelineHandle = VkPipeline.NULL,
                basePipelineIndex = -1
            )
            logger.info { "graphic pipelines creating" }
            try {
                graphicsPipelines = struct_logicaldevice.device.createGraphicsPipeline(
                    pipelineCache = VkPipelineCache.NULL,
                    createInfos = arrayOf(graphicsPipelineCI)
                )

                logger.info { "graphic pipelines: ${graphicsPipelines.size}" }
                graphicsPipelines.indices.forEach {
                    assert(graphicsPipelines[it].isValid)
                }

            } catch (e: Exception) {
                logger.info { e.localizedMessage }
            }


//crash on destroy  //No longer crash, cause shadermodule load correct
            struct_logicaldevice.device.destroy(shadermodule_vert)
            struct_logicaldevice.device.destroy(shadermodule_frag)
        }
    }

    object struct_framebuffer {
        val swapchainFramebuffers: VkFramebuffer_Array

        init {
//struct_imageview.swapchainImageViews.size
            val framebufferCI = FramebufferCreateInfo(
                renderPass = struct_renderpass.renderPass,
//                attachments = struct_imageview.swapchainImageViews,
                width = struct_swapchain.extent.width,
                height = struct_swapchain.extent.height,
                layers = 1  //Image layer, not debug layer
            )
            swapchainFramebuffers = struct_logicaldevice.device.createFramebufferArray(
                createInfo = framebufferCI,
                imageViews = struct_imageview.swapchainImageViews
            )

        }

    }

    object struct_commandpool {

        val commandpool: VkCommandPool

        init {
            val queuefamilyGraphic =
                struct_physicaldevice.physicalDevice_wrapper.surfaceSwapChainSupport.queuefamily_graphic
            val commandPoolCI = CommandPoolCreateInfo(
                queueFamilyIndex = queuefamilyGraphic,
                flags = 0
            )
            commandpool = struct_logicaldevice.device.createCommandPool(commandPoolCI)
        }

    }

    object struct_commandbuffers {
        val logger = KotlinLogging.logger { }

        val commandbuffers : Array<CommandBuffer>

        init {
//            struct_framebuffer.swapchainFramebuffers.size

            val commandBufferAllocateInfo = CommandBufferAllocateInfo(
                commandPool = struct_commandpool.commandpool,
                level = VkCommandBufferLevel.PRIMARY,
                commandBufferCount = struct_framebuffer.swapchainFramebuffers.size
            )
            commandbuffers = struct_logicaldevice.device.allocateCommandBuffers(
                allocateInfo = commandBufferAllocateInfo
            )

            commandbuffers.forEachIndexed { i,it->
                val commandbufferBeginInfo = CommandBufferBeginInfo(
                    flags = 0,
                    inheritanceInfo = null
                )
                it.begin(commandbufferBeginInfo)    //reset buffer

                val renderPassBeginInfo = RenderPassBeginInfo(
                    renderPass = struct_renderpass.renderPass,
                    framebuffer = struct_framebuffer.swapchainFramebuffers[i],
                    renderArea = Rect2D(
                        offset = Offset2D(0, 0),
                        extent = struct_swapchain.extent
                    ),
                    clearValues = arrayOf(ClearValue(0.0f, 0.0f, 0.0f, 1.0f))
                )
                it.beginRenderPass(
                    renderPassBegin = renderPassBeginInfo,
                    contents = VkSubpassContents.INLINE
                )
                it.bindPipeline(
                    pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                    pipeline = struct_graphicpipeline.graphicsPipelines[0]
                )
                it.draw(
                    vertexCount = 3,
                    instanceCount = 1,
                    firstVertex = 0,
                    firstInstance = 0
                )
                it.endRenderPass()
                logger.info {
                    it.end().description
                }
            }

        }

    }

    object struct_semaphores {
        val max_frames_in_flight = 2
        val imageAvailable: Array<VkSemaphore>
        val renderFinished: Array<VkSemaphore>

        val inFlightFences: Array<VkFence>
        val imagesInFlight: Array<VkFence>
        init {
            val semaphoreCI = SemaphoreCreateInfo()
            imageAvailable = Array(max_frames_in_flight) {
                struct_logicaldevice.device.createSemaphore(semaphoreCI)
            }
            renderFinished = Array(max_frames_in_flight) {
                struct_logicaldevice.device.createSemaphore(semaphoreCI)
            }

            val fenceCI = FenceCreateInfo(
                flags = VkFenceCreate.SIGNALED_BIT.i
            )
            inFlightFences = Array(max_frames_in_flight) {
                struct_logicaldevice.device.createFence(fenceCI)
            }
            imagesInFlight = Array(struct_swapchain.swapChainImages.size) { VkFence.NULL }

        }

    }

    fun mainLoop() {
        //autoswap implied
        struct_window.window.loop(Consumer {
//            logger.info("a")
            drawFrame()
        })
        struct_logicaldevice.device.waitIdle()
    }

    var currentFrame = 0

    fun drawFrame() {
        //index of swapchainImages
        val index = currentFrame % struct_semaphores.max_frames_in_flight
        struct_logicaldevice.device.waitForFences(
            fence = struct_semaphores.inFlightFences[index],
            waitAll = true, //wait any when false
            timeout = -1
        )

        val imageIndex = struct_logicaldevice.device.acquireNextImageKHR(
            swapchain = struct_swapchain.swapchain,
            timeout = -1L,
            semaphore = struct_semaphores.imageAvailable[index],
            fence = VkFence.NULL
//            check = ::defaultCheck
        )

        if (struct_semaphores.imagesInFlight[imageIndex] != VkFence.NULL) {
            struct_logicaldevice.device.waitForFences(
                fence = struct_semaphores.imagesInFlight[imageIndex],
                waitAll = true, //wait any when false
                timeout = -1
            )
        }
        struct_semaphores.imagesInFlight[imageIndex] = struct_semaphores.inFlightFences[index]

        val submitInfo = SubmitInfo(
            waitSemaphoreCount = 1,
            waitSemaphores = VkSemaphore_Array(arrayListOf(struct_semaphores.imageAvailable[index])),
            waitDstStageMask = intArrayOf(VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT),
            commandBuffers = arrayOf(struct_commandbuffers.commandbuffers[imageIndex]),
            signalSemaphores = VkSemaphore_Array(arrayListOf(struct_semaphores.renderFinished[index]))
        )
        // That means that theoretically the implementation can already start executing our vertex shader and such while the image is not yet available.
        // Each entry in the waitStages array corresponds to the semaphore with the same index in pWaitSemaphores.

//        logger.info {
//            val s = "submit: "

        struct_logicaldevice.device.resetFences(struct_semaphores.inFlightFences[index])

        struct_logicaldevice.graphicsQueue.submit(
                submit = submitInfo,
                fence = struct_semaphores.inFlightFences[index]
            ).description
//        }

        val presentInfoKHR = PresentInfoKHR(
            waitSemaphores = VkSemaphore_Array(arrayListOf(struct_semaphores.renderFinished[index])),
            swapchains = VkSwapchainKHR_Array(arrayListOf(struct_swapchain.swapchain)),
            imageIndices = intArrayOf(imageIndex),
            results = null
        )
//        logger.info {
//            val s = "present: "
        struct_logicaldevice.presentQueue.presentKHR(presentInfoKHR).description




//        logger.info {
            currentFrame++
//        }
    }

    private fun cleanup() {
        struct_semaphores.inFlightFences.forEach { struct_logicaldevice.device.destroy(it) }
        struct_semaphores.renderFinished.forEach { struct_logicaldevice.device.destroy(it) }
        struct_semaphores.imageAvailable.forEach { struct_logicaldevice.device.destroy(it) }
        struct_logicaldevice.device.destroy(struct_commandpool.commandpool)
        struct_framebuffer.swapchainFramebuffers.indices.forEach {
            struct_logicaldevice.device.destroy(struct_framebuffer.swapchainFramebuffers[it])
        }
        struct_graphicpipeline.graphicsPipelines.indices.forEach {
            struct_logicaldevice.device.destroy(struct_graphicpipeline.graphicsPipelines[it])
        }
        struct_logicaldevice.device.destroy(struct_graphicpipeline.pipelineLayout)
        struct_logicaldevice.device.destroy(struct_renderpass.renderPass)
        struct_imageview.swapchainImageViews.indices.forEach { struct_logicaldevice.device.destroy(struct_imageview.swapchainImageViews[it]) }
//        swapChainImageViews.forEach { device.destroy(it) }
        struct_logicaldevice.device.destroy(struct_swapchain.swapchain)
        struct_logicaldevice.device.destroy()
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
