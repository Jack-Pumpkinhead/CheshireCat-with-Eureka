package vulkan.concurrent

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.select
import mu.KotlinLogging
import vkk.entities.*
import vkk.identifiers.CommandBuffer
import vkk.vk10.createFramebuffer
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.FramebufferCreateInfo
import vulkan.OzDevice
import vulkan.OzRenderPass
import vulkan.OzVulkan
import vulkan.command.OzCB

class OzFramebuffer(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val renderpass: OzRenderPass,
    val imageviewArray: VkImageView_Array,
    extent2D: Extent2D
) {

    constructor(
        ozVulkan: OzVulkan,
        device: OzDevice,
        renderpass: OzRenderPass,
        imageView: VkImageView,
        extent2D: Extent2D
    ) : this(ozVulkan, device, renderpass, VkImageView_Array(1) { imageView }, extent2D)

    companion object {
        val logger = KotlinLogging.logger { }
    }

    val framebuffer = device.device.createFramebuffer(
        FramebufferCreateInfo(
            renderPass = renderpass.renderpass,
            attachments = imageviewArray,
            width = extent2D.width,
            height = extent2D.height,
            layers = 1  //Image layer, not debug layer
        )
    )



    sealed class Action {
        class RegisterDraw(val cbs: Array<CommandBuffer>) : Action()
        class UnRegisterDraw(val cbs: Array<CommandBuffer>) : Action()
    }
    private val wait_clear = Channel<Triple<Job, CompletableJob, CompletableJob>>(Channel.UNLIMITED)
    private val getCmds = Channel<CompletableDeferred<Array<CommandBuffer>>>(Channel.UNLIMITED)

    suspend fun wait_clear(toWait: Job): Job {
        val received = Job()
        val reset = Job()
        wait_clear.send(Triple(toWait, received, reset))
        received.join()
        return reset
    }

    suspend fun getCmds(): CompletableDeferred<Array<CommandBuffer>> {
        val resp = CompletableDeferred<Array<CommandBuffer>>()
        getCmds.send(resp)
        return resp
    }

    val actor = device.scope.actor<Action> {
        while (isActive) {
            select<Unit> {
                wait_clear.onReceive { (toWait, received, clear) -> //两次握手
                    received.complete()
                    toWait.join()
                    drawCmds.clear()
                    clear.complete()
                }
                getCmds.onReceive{ resp->
                    resp.complete(drawCmds.toTypedArray())
                }
                channel.onReceive {
                    when (it) {
                        is Action.RegisterDraw -> drawCmds += it.cbs
                        is Action.UnRegisterDraw -> drawCmds -= it.cbs
                    }
                }
            }
        }
    }

    val drawCmds = mutableListOf<CommandBuffer>()


    fun recordDraw(
        cb: CommandBuffer,
        buffers: VkBuffer_Array,
        offsets: VkDeviceSize_Array = VkDeviceSize_Array(buffers.size) { VkDeviceSize(0) },
        indexBuffer: VkBuffer,
        count: Int
    ): CommandBuffer = OzCB.recordDrawIndexed(
        cb,
        renderpass.renderpass,
        framebuffer,
        Extent2D(ozVulkan.window.framebufferSize),
        ozVulkan.pipeline.graphicsPipelines[0],
        buffers,
        offsets,
        indexBuffer,
        count
    )



    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(renderpass::destroy, this::destroy)
    }

    fun destroy() {
        device.device.destroy(framebuffer)
    }

}