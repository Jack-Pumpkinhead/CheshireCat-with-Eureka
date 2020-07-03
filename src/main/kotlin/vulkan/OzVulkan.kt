package vulkan

import game.main.Univ
import game.window.OzWindow
import glm_.vec2.Vec2i
import kotlinx.coroutines.Job
import kotlinx.coroutines.joinAll
import mu.KotlinLogging
import org.springframework.beans.factory.getBean
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import vkk.entities.VkSwapchainKHR
import vkk.vk10.structs.Extent2D
import vulkan.command.CopyBuffer
import vulkan.buffer.OzVMA
import vulkan.command.DrawCmd
import vulkan.drawing.OzObjects
import vulkan.image.OzImages
import vulkan.image.Samplers
import vulkan.pipelines.*
import vulkan.pipelines.descriptor.LayoutMVP
import vulkan.pipelines.descriptor.SetLayouts
import vulkan.pipelines.descriptor.TextureSets
import vulkan.pipelines.pipelineLayout.OzUniformMatrixDynamic
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.util.DMs
import vulkan.util.LoaderGLSL
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/4/20 13:00
 */
class OzVulkan(val univ: Univ, val window: OzWindow) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val context = GenericApplicationContext(univ.context)

    var instance: OzInstance
    var surface: OzSurface
    var physicalDevices: OzPhysicalDevices
    var physicalDevice: OzPhysicalDevice
    var surfaceSupport: SurfaceSwapchainSupport
    var device: OzDevice
    var queues: OzQueues
    var commandpools: OzCommandPools
    var copybuffer: CopyBuffer

    var vma: OzVMA
    var images: OzImages
    var imageViews: OzImageViews

    var shadermodules:OzShaderModules
    var seyLayouts:SetLayouts
    var descriptorPools:OzDescriptorPools
    var pipelineLayouts:OzPipelineLayouts
    var uniformMatrixDynamic:OzUniformMatrixDynamic
    var dms: DMs
    var layoutMVP:LayoutMVP
    var samplers:Samplers
    var textureSets:TextureSets

    init {
        //default name / destroyMethodName
        val mainBeans = beans {
//            bean<OzWindow> { window }
            bean<OzInstance>(destroyMethodName = "destroy")
            bean<OzSurface>(destroyMethodName = "destroy")
            bean<OzPhysicalDevices>()
            bean() { ref<OzPhysicalDevices>().physicalDevice }
            bean() { ref<OzPhysicalDevices>().surfaceSupport }
            bean<OzDevice>(destroyMethodName = "destroy")
            bean<OzQueues>(destroyMethodName = "destroy")
            bean<OzCommandPools>(destroyMethodName = "destroy")
            bean<CopyBuffer>()

            bean<OzVMA>(destroyMethodName = "destroy")

            bean<OzImageViews>()


        }

        val pipelineBeans = beans() {
            bean<OzShaderModules>(destroyMethodName = "destroy")
            bean<SetLayouts>(destroyMethodName = "destroy")
            bean<OzDescriptorPools>(destroyMethodName = "destroy")
            bean<OzPipelineLayouts>(destroyMethodName = "destroy")

            bean<OzUniformMatrixDynamic>(destroyMethodName = "destroy")

        }
        val extraBeans = beans() {
            bean<OzObjects>()
            bean<DMs>()
            bean<LayoutMVP>(destroyMethodName = "destroy")
            bean<Samplers>(destroyMethodName = "destroy")
            bean<OzImages>(destroyMethodName = "destroy")

            bean<TextureSets>(destroyMethodName = "destroy")

        }
        mainBeans.initialize(context)
        pipelineBeans.initialize(context)
        extraBeans.initialize(context)
        context.refresh()


        instance = context.getBean()
        surface = context.getBean()
        physicalDevices = context.getBean()
        physicalDevice = context.getBean()
        surfaceSupport = context.getBean()
        device = context.getBean()
        queues = context.getBean()
        commandpools = context.getBean()
        copybuffer = context.getBean()

        vma = context.getBean()
        imageViews = context.getBean()
        shadermodules = context.getBean()
        seyLayouts = context.getBean()
        descriptorPools = context.getBean()
        pipelineLayouts = context.getBean()
        uniformMatrixDynamic = context.getBean()
        dms = context.getBean()
        layoutMVP = context.getBean()
        samplers = context.getBean()
        images = context.getBean()
        textureSets = context.getBean()

    }


    var swapchainContext = GenericApplicationContext(context)


    var oldSwapchain: VkSwapchainKHR = VkSwapchainKHR.NULL
    var framebufferSize: Extent2D
    var swapchain:OzSwapchain
    var renderPass:OzRenderPasses
    var graphicPipelines:OzGraphicPipelines
    var framebuffers:OzFramebuffers
//    var drawImage:DrawImage
    var drawCmd:DrawCmd


    val swapchainBeans = beans {
        bean<Extent2D> { Extent2D(ref<OzWindow>().framebufferSize) }
        bean<OzSwapchain>(destroyMethodName = "destroy") {
            OzSwapchain(ref(), ref(), ref(),
                oldSwapchain, ref(), ref(), ref(), ref()
            )
        }

        bean<OzRenderPasses>(destroyMethodName = "destroy") {
            OzRenderPasses(ref(), ref<SurfaceSwapchainSupport>().surfaceFormat.format)
        }

        bean<OzGraphicPipelines>(destroyMethodName = "destroy")
        bean<OzFramebuffers>(destroyMethodName = "destroy")

//        bean<DrawImage>(destroyMethodName = "destroy")
        bean<DrawCmd>()

    }

    init {
        swapchainBeans.initialize(swapchainContext)
        swapchainContext.refresh()
        oldSwapchain = swapchainContext.getBean<OzSwapchain>().swapchain

        framebufferSize = swapchainContext.getBean()
        swapchain = swapchainContext.getBean()
        renderPass = swapchainContext.getBean()
        graphicPipelines = swapchainContext.getBean()
        framebuffers = swapchainContext.getBean()
//        drawImage = swapchainContext.getBean()
        drawCmd = swapchainContext.getBean()
    }

    fun recreateSwapchainContext() {
        val swapchainContext_ = GenericApplicationContext(context)
        swapchainBeans.initialize(swapchainContext_)
        swapchainContext_.refresh()
        swapchainContext.close()
        swapchainContext = swapchainContext_
        oldSwapchain = swapchainContext.getBean<OzSwapchain>().swapchain

        framebufferSize = swapchainContext.getBean()
        swapchain = swapchainContext.getBean()
        renderPass = swapchainContext.getBean()
        graphicPipelines = swapchainContext.getBean()
        framebuffers = swapchainContext.getBean()
//        drawImage = swapchainContext.getBean()
        drawCmd = swapchainContext.getBean()
    }


    var shouldRecreate = false

    suspend fun recreateSwapchain(windowSize: Vec2i) {
        val job = Job()


//        val cps = swapchainContext.getBean<OzCommandPools>().onRecreateSwapchain(job)
//        val qs = swapchainContext.getBean<OzDescriptorPools>().onRecreateSwapchain(job)
//        val fbs = swapchainContext.getBean<OzQueues>().onRecreateRenderpass(job)
        val cps = swapchainContext.getBean<OzCommandPools>().cps.map { it.waitComplete() }
        val qs = swapchainContext.getBean<OzDescriptorPools>().pools.map { it.waitComplete() }
        val fbs = swapchainContext.getBean<OzQueues>().qs.map { it.waitComplete() }
        cps.joinAll()
        qs.joinAll()
        fbs.joinAll()

        swapchainContext.getBean<OzDevice>().device.waitIdle()


        recreateSwapchainContext()

        job.complete()  //trigger waiting coroutine //nothing to trigger now


//        afterSwapchainRecreateEvent.forEach {
//            it.invoke()
//        }

        univ.events.afterRecreateSwapchain.send(windowSize)
//        swapchainContext.getBean<OzObjects>().getObjects().forEach {
//            it.data.afterSwapchainRecreated()
//        }

    }

//    val afterSwapchainRecreateEvent = mutableListOf<AfterSwapchainRecreated>()

    fun destroy() {
        swapchainContext.getBean<OzDevice>().device.waitIdle()
        swapchainContext.close()
        context.close()
        LoaderGLSL.destroy()
    }

    init {
//        VmaAllocationCreateInfo
//        Vma.
//        Vma.vmaCreateBuffer()
//        logger.info {
//            "maxMemoryAllocationCount: ${physicalDevice.properties.limits.maxMemoryAllocationCount}"
//        }
    }
}