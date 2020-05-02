package vulkan

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import mu.KotlinLogging
import vkk.VkCommandBufferLevel
import vkk.VkCommandBufferUsage
import vkk.VkCommandPoolCreate
import vkk.entities.VkBuffer
import vkk.entities.VkCommandPool
import vkk.entities.VkDeviceSize
import vkk.vk10.*
import vkk.vk10.structs.*
import vulkan.concurrent.OzCommandPool
import vulkan.util.SurfaceSwapchainSupport

class OzCommandPools(val ozVulkan: OzVulkan, val device: OzDevice, val surfaceSupport: SurfaceSwapchainSupport) {

    val logger = KotlinLogging.logger { }

    val graphic: VkCommandPool = device.device.createCommandPool(
        CommandPoolCreateInfo(
            queueFamilyIndex = surfaceSupport.queuefamily_graphic,
            flags = VkCommandPoolCreate(0).i
        )
    )
    val transfer: VkCommandPool = device.device.createCommandPool(
        CommandPoolCreateInfo(
            queueFamilyIndex = surfaceSupport.queuefamily_transfer,
            flags = VkCommandPoolCreate.TRANSIENT_BIT.i //allocate short-lived buffers
        )
    )


    val graphicCP = OzCommandPool(ozVulkan, device, surfaceSupport.queuefamily_graphic, 0)
    val transferCP = OzCommandPool(ozVulkan, device, surfaceSupport.queuefamily_transfer, VkCommandPoolCreate.TRANSIENT_BIT.i)

    suspend fun onRecreateRenderpass(job: CompletableJob): List<Pair<CompletableJob, CompletableJob>> {
        return listOf(
            graphicCP.wait_reset(job),
            transferCP.wait_reset(job)
        )
    }

    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        device.device.destroy(transfer)
        device.device.destroy(graphic)
    }

    fun copyBuffer(src: Long, dst: Long, size: Int) = copyBuffer(VkBuffer(src), VkBuffer(dst), size)

    fun copyBuffer(src: VkBuffer, dst: VkBuffer, size: Int) {
        val cbs = device.device.allocateCommandBuffers(
            allocateInfo = CommandBufferAllocateInfo(
                commandPool = transfer,
                level = VkCommandBufferLevel.PRIMARY,
                commandBufferCount = 1
            )
        )
        cbs[0].begin(
            CommandBufferBeginInfo(
                flags = VkCommandBufferUsage.ONE_TIME_SUBMIT_BIT.i
            )
        )
        val bufferCopy = BufferCopy(
            srcOffset = VkDeviceSize(0),
            dstOffset = VkDeviceSize(0),
            size = VkDeviceSize(size)
        )
        cbs[0].copyBuffer(
            srcBuffer = src,
            dstBuffer = dst,
            regions = arrayOf(bufferCopy)
        )

        cbs[0].end()
        val submitInfo = SubmitInfo(
            commandBuffers = cbs
        )
        device.transferQueue.submit(submitInfo)
        device.transferQueue.waitIdle() //can use fence to wait multiple transfers simultaneously
        device.device.freeCommandBuffers(transfer,cbs)

    }



}