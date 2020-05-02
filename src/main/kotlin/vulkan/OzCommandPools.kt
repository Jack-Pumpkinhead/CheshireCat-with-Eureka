package vulkan

import kotlinx.coroutines.CompletableJob
import mu.KotlinLogging
import vkk.VkCommandPoolCreate
import vulkan.concurrent.OzCommandPool
import vulkan.util.SurfaceSwapchainSupport

class OzCommandPools(val ozVulkan: OzVulkan, val device: OzDevice, surfaceSupport: SurfaceSwapchainSupport) {

    companion object{
        val logger = KotlinLogging.logger { }
    }


    val graphicCP = OzCommandPool(ozVulkan, device, surfaceSupport.queuefamily_graphic, 0)
    val transferCP = OzCommandPool(ozVulkan, device, surfaceSupport.queuefamily_transfer, VkCommandPoolCreate.TRANSIENT_BIT.i)

    suspend fun onRecreateRenderpass(job: CompletableJob): List<Pair<CompletableJob, CompletableJob>> {
        return listOf(
            graphicCP.wait_reset(job),
            transferCP.wait_reset(job)
        )
    }



}