package vulkan

import game.main.AfterSwapchainRecreated
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
import vulkan.drawing.DrawImage
import vulkan.drawing.OzObjects
import vulkan.pipelines.*
import vulkan.pipelines.layout.OzDescriptorSetLayouts
import vulkan.pipelines.layout.OzUniformMatrixDynamic
import vulkan.pipelines.layout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.OzVertexInputs
import vulkan.util.LoaderGLSL
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/4/20 13:00
 */
class OzVulkan(val univ: Univ, val window: OzWindow) {

    companion object {

        val logger = KotlinLogging.logger {  }

    }

    val context = GenericApplicationContext()

    init {
        //default name / destroyMethodName
        val mainBeans = beans {
            bean<OzWindow>{ window}
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






        }
        val pipelineBeans = beans() {
            bean<OzShaderModules>(destroyMethodName = "destroy")
            bean<OzVertexInputs>()
            bean<OzDescriptorSetLayouts>(destroyMethodName = "destroy")
            bean<OzDescriptorPools>(destroyMethodName = "destroy")
            bean<OzPipelineLayouts>(destroyMethodName = "destroy")

            bean<OzUniformMatrixDynamic>(destroyMethodName = "destroy")

        }
        val extraBeans = beans() {
            bean<OzObjects>()

        }
        mainBeans.initialize(context)
        pipelineBeans.initialize(context)
        extraBeans.initialize(context)
        context.refresh()



    }


    var swapchainContext = GenericApplicationContext(context)

    var oldSwapchain: VkSwapchainKHR = VkSwapchainKHR.NULL

    val swapchainBeans = beans {
        bean<Extent2D> { Extent2D(ref<OzWindow>().framebufferSize) }
        bean<OzSwapchain>(destroyMethodName = "destroy") { OzSwapchain(ref(), ref(), ref(), oldSwapchain) }
        bean<OzRenderPass>(destroyMethodName = "destroy") {
            OzRenderPass(ref(), ref<SurfaceSwapchainSupport>().surfaceFormat.format)
        }
        bean<OzImageViews>(destroyMethodName = "destroy")

        bean<OzGraphicPipelines>(destroyMethodName = "destroy")
        bean<OzFramebuffers>(destroyMethodName = "destroy")

        bean<DrawImage>(destroyMethodName = "destroy")

    }

    init {
        swapchainBeans.initialize(swapchainContext)
        swapchainContext.refresh()
        oldSwapchain = swapchainContext.getBean<OzSwapchain>().swapchain
    }

    fun recreateSwapchainContext() {
        val swapchainContext_ = GenericApplicationContext(context)
        swapchainBeans.initialize(swapchainContext_)
        swapchainContext_.refresh()
        swapchainContext.close()
        swapchainContext = swapchainContext_
        oldSwapchain = swapchainContext.getBean<OzSwapchain>().swapchain
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
        swapchainContext.getBean<OzObjects>().getObjects().forEach {
            it.data.afterSwapchainRecreated()
        }

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