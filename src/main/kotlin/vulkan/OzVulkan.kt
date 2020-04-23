package vulkan

import com.google.common.graph.GraphBuilder
import com.google.common.graph.Traverser
import game.main.CleanUpMethod
import game.main.Univ
import game.window.OzWindow
import glm_.vec2.Vec2i
import mu.KotlinLogging
import uno.glfw.glfw
import vkk.vk10.structs.Extent2D
import vulkan.util.LoaderGLSL
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/4/20 13:00
 */
class OzVulkan(val univ: Univ, val window: OzWindow) {
    val logger = KotlinLogging.logger {  }
    val cleanups = GraphBuilder.directed().allowsSelfLoops(false).build<CleanUpMethod>()
    val instance = OzInstance(this)
    val surface = OzSurface(this, instance, window)
    var physicalDevice = OzPhysicalDevice(this, instance, instance.physicalDevices[0])
    var surfaceSupport = SurfaceSwapchainSupport(physicalDevice.pd, surface.surface)

    init {
        var i = 1
        while (!(physicalDevice.supported() && surfaceSupport.supported()) && i < instance.physicalDevices.size) {
            physicalDevice = OzPhysicalDevice(this, instance, instance.physicalDevices[i])
            surfaceSupport = SurfaceSwapchainSupport(physicalDevice.pd, surface.surface)
            i++
        }
        if (!(physicalDevice.supported() && surfaceSupport.supported())) {
            logger.warn { "No suitable physical device found." }
        }
    }

    val device = OzDevice(this, physicalDevice, surfaceSupport)

    var swapchain = OzSwapchain(this, surfaceSupport, device, window.framebufferSize)

    var imageViews = OzImageViews(this, device, swapchain)

    var renderpass = OzRenderPass(this, device, swapchain)

    var pipeline = OzGraphicPipeline(this, device, renderpass, swapchain)

    var framebuffer = OzFramebuffers(this, device, renderpass, swapchain, imageViews)

    var commandPool = OzCommandPool(this, device, surfaceSupport)

    var commandBuffers = OzCommandBuffers(this, device, commandPool, framebuffer, swapchain, pipeline, renderpass)

    var sync = OzSyncObject(this, device, swapchain)


    fun recreateRenderpass(windowSize: Vec2i) {

        cleanup(framebuffer::destroy)
        commandBuffers.destroy()
        cleanup(pipeline::destroy)
        cleanup(renderpass::destroy)
        cleanup(imageViews::destroy)
        cleanup(swapchain::destroy)

        swapchain = OzSwapchain(this, surfaceSupport, device, windowSize)

        imageViews = OzImageViews(this, device, swapchain)

        renderpass = OzRenderPass(this, device, swapchain)

        pipeline = OzGraphicPipeline(this, device, renderpass, swapchain)

        framebuffer = OzFramebuffers(this, device, renderpass, swapchain, imageViews)

        commandBuffers = OzCommandBuffers(this, device, commandPool, framebuffer, swapchain, pipeline, renderpass)


    }



    fun cleanup(c: CleanUpMethod) {
        if (cleanups.nodes().contains(c)) {
            val unders = mutableSetOf<CleanUpMethod>()
            Traverser.forGraph(cleanups).depthFirstPostOrder(c).forEach { it.invoke(); unders += it }
            unders.forEach{ cleanups.removeNode(it)}
        }
    }

    fun destroy() {
        device.device.waitIdle()
        while (cleanups.nodes().isNotEmpty()) {
            cleanup(cleanups.nodes().first())
        }
        LoaderGLSL.destroy()
    }
}