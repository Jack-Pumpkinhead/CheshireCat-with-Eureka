package vulkan

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import com.google.common.graph.Traverser
import game.main.AfterSwapchainRecreated
import game.main.CleanUpMethod
import game.main.Univ
import game.window.OzWindow
import glm_.vec2.Vec2i
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import vkk.entities.VkBuffer
import vkk.vk10.structs.Extent2D
import vulkan.command.OzCB
import vulkan.drawing.OzUniformBuffer
import vulkan.drawing.OzVMA
import vulkan.pipelines.*
import vulkan.util.LoaderGLSL
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/4/20 13:00
 */
class OzVulkan(val univ: Univ, val window: OzWindow) {
    val logger = KotlinLogging.logger {  }
    val cleanups: MutableGraph<CleanUpMethod> = GraphBuilder.directed().allowsSelfLoops(false).build<CleanUpMethod>()
    val instance = OzInstance(this)
    val surface = OzSurface(this, instance, window)
    var physicalDevice = OzPhysicalDevice(this, instance, instance.physicalDevices[0])
    var surfaceSupport = SurfaceSwapchainSupport(physicalDevice, surface.surface)

    init {
        var i = 1
        while (!(physicalDevice.supported() && surfaceSupport.supported()) && i < instance.physicalDevices.size) {
            physicalDevice = OzPhysicalDevice(this, instance, instance.physicalDevices[i])
            surfaceSupport = SurfaceSwapchainSupport(physicalDevice, surface.surface)
            i++
        }
        if (!(physicalDevice.supported() && surfaceSupport.supported())) {
            logger.warn { "No suitable physical device found." }
        }
        logger.info {
            "use pd: ${physicalDevice.pd.address()}"
        }
    }

    val device = OzDevice(this, physicalDevice, surfaceSupport)

    val commandPool = OzCommandPools(this, device, surfaceSupport)

    val cb = OzCB(this, commandPool, device)

    val shadermodule = OzShaderModule(this, device)

    val descriptorPool = OzDescriptorPool(this, device, 3)





    val vma = OzVMA(this, physicalDevice, device)



    var swapchain = OzSwapchain(this, surfaceSupport, device, window.framebufferSize)

    var imageViews = OzImageViews(this, device, swapchain)

    var renderpass = OzRenderPass(this, device, surfaceSupport.surfaceFormat.format)

    val graphicPipelines = OzGraphicPipelines(this, device, shadermodule, renderpass, Extent2D(window.framebufferSize))

    var framebuffer = OzFramebuffers(this, device, renderpass, imageViews, Extent2D(window.framebufferSize))


//    var commandBuffers = OzCommandBuffers(this, device, commandPool, framebuffer, swapchain, pipeline, renderpass)

    val uniformBuffer = OzUniformBuffer(this, device, vma, 3)

    val descriptorSets = OzDescriptorSets(
        this,
        device,
        descriptorPool,
        graphicPipelines.hellomvp,
        3,
        uniformBuffer.buffers.map { VkBuffer(it.pBuffer) },
        4 * 4 * 3 * Long.SIZE_BYTES
    )





    var shouldRecreate = false

    fun recreateRenderpass(windowSize: Vec2i) {
        val job = Job()
        val cps = runBlocking {
            commandPool.onRecreateRenderpass(job)
        }
        val qs = runBlocking {
            device.onRecreateRenderpass(job)
        }
        val fbs = runBlocking {
            framebuffer.onRecreateRenderpass(job)
        }
        runBlocking {
            cps.map { it.first }.joinAll()
        }



        val newSwapchain = OzSwapchain(this, surfaceSupport, device, windowSize, swapchain.swapchain)

        device.device.waitIdle()

        graphicPipelines.recreate(Extent2D(windowSize))

        framebuffer.fbs.forEach {
            cleanup(it::destroy)
        }
//        cleanup(renderpass::destroy)
        cleanup(imageViews::destroy)
        cleanup(swapchain::destroy)

        swapchain = newSwapchain

        imageViews = OzImageViews(this, device, swapchain)//

//        renderpass = OzRenderPass(this, device, swapchain)

        framebuffer = OzFramebuffers(this, device, renderpass, imageViews, Extent2D(windowSize))//

        job.complete()  //trigger waiting coroutine

        runBlocking {
            cps.map { it.second }.joinAll()
            qs.joinAll()
            fbs.joinAll()
        }

        //TODO: poll event to drawable things

        after.forEach {
            it.invoke()
        }

    }

    val after = mutableListOf<AfterSwapchainRecreated>()


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

    init {
//        VmaAllocationCreateInfo
//        Vma.
//        Vma.vmaCreateBuffer()
        logger.info {
            "maxMemoryAllocationCount: ${physicalDevice.properties.limits.maxMemoryAllocationCount}"
        }
    }
}