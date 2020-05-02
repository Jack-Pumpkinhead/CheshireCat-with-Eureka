package vulkan

import com.google.common.graph.GraphBuilder
import com.google.common.graph.Traverser
import game.main.AfterSwapchainRecreated
import game.main.CleanUpMethod
import game.main.Univ
import game.window.OzWindow
import glm_.vec2.Vec2i
import kool.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import vkk.entities.VkBuffer
import vkk.entities.VkDeviceSize
import vkk.memCopy
import vkk.vk10.structs.Extent2D
import vulkan.command.OzCB
import vulkan.drawing.OzVMA
import vulkan.pipelines.OzGraphicPipeline
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

    var commandPool = OzCommandPools(this, device, surfaceSupport)

    val cb = OzCB(this, commandPool, device)


    val vma = OzVMA(this, physicalDevice, device)

    var swapchain = OzSwapchain(this, surfaceSupport, device, window.framebufferSize)

    var imageViews = OzImageViews(this, device, swapchain)

    var renderpass = OzRenderPass(this, device, surfaceSupport.surfaceFormat.format)

    var pipeline = OzGraphicPipeline(this, device, renderpass, Extent2D(window.framebufferSize))

    var framebuffer = OzFramebuffers(this, device, renderpass, imageViews, Extent2D(window.framebufferSize))

//    var vertexBuffer = OzVertexBuffer.of(device, (3 + 3) * Float.BYTES * 3)

//    var vertexBuffer = OzVertexBuffer(this, device, physicalDevice, (3 + 3) * Float.BYTES * 3)

    var vb_s = vma.of_staging((3 + 3) * Float.BYTES * 3)
    var vb_d = vma.of_VertexBuffer_device_local((3 + 3) * Float.BYTES * 3)

    init {
        fillBuffer()
    }

    fun fillBuffer() {
        /*Stack{
            val arr = it.floats(
                // position    color
                +0.0f, -0.5f, +0f, 1f, 0f, 0f,
                +0.5f, +0.5f, +0f, 0f, 1f, 0f,
                -0.5f, 0.5f, +0f, 0f, 0f, 1f)

            logger.info {
                "arr.remSize: ${arr.remSize}"
            }
            device.device.mappedMemory(
                memory = vertexBuffer_staging.memory,
                offset = VkDeviceSize(0),
                size = VkDeviceSize(vertexBuffer_staging.bytes),
                flags = 0
            ) {
                memCopy(MemoryUtil.memAddress(arr), it, VkDeviceSize(arr.remSize.L))
            }
        }*/
//        commandPool.copyBuffer(vertexBuffer_staging, vertexBuffer_device_local, vertexBuffer_staging.bytes)
//        cleanup(vertexBuffer_staging::destroy)
//        vertexBuffer_staging.destroy()
        Stack{
            val arr = it.floats(
                // position    color
                +0.0f, -0.5f, +0f, 1f, 1f, 1f,
                +0.5f, +0.5f, +0f, 0f, 1f, 0f,
                -0.5f, 0.5f, +0f, 0f, 0f, 1f)

            logger.info {
                "arr.remSize: ${arr.remSize}"
            }
            vb_s.withMap {
                memCopy(arr.adr, it, VkDeviceSize(arr.remSize))
            }
            commandPool.copyBuffer(VkBuffer(vb_s.pBuffer), VkBuffer(vb_d.pBuffer), arr.remSize)

        }

    }


    var commandBuffers = OzCommandBuffers(this, device, commandPool, framebuffer, swapchain, pipeline, renderpass)

    var sync = OzSyncObject(this, device, swapchain)


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
        cleanup(framebuffer::destroy)
        commandBuffers.destroy()
        cleanup(pipeline::destroy)
//        cleanup(renderpass::destroy)
        cleanup(imageViews::destroy)
        cleanup(swapchain::destroy)

        swapchain = newSwapchain

        imageViews = OzImageViews(this, device, swapchain)//

//        renderpass = OzRenderPass(this, device, swapchain)

        pipeline = OzGraphicPipeline(this, device, renderpass, Extent2D(windowSize))

        framebuffer = OzFramebuffers(this, device, renderpass, imageViews, Extent2D(windowSize))//

        commandBuffers = OzCommandBuffers(this, device, commandPool, framebuffer, swapchain, pipeline, renderpass)

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