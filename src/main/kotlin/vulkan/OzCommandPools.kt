package vulkan

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import vkk.VkCommandPoolCreate
import vkk.vk10.createCommandPool
import vkk.vk10.structs.CommandPoolCreateInfo
import vulkan.concurrent.OzCommandPool

class OzCommandPools(val device: OzDevice) {

    //can create multiple command pools
    val graphicCP = of(
        flags = VkCommandPoolCreate.RESET_COMMAND_BUFFER_BIT.i,
        queueFamilyIndex = device.surfaceSupport.queuefamily_graphic
    )
    val graphicMutableCP = of(queueFamilyIndex = device.surfaceSupport.queuefamily_graphic)
    val transferCP = of(VkCommandPoolCreate.TRANSIENT_BIT.i, device.surfaceSupport.queuefamily_transfer)

    val cps = listOf(
        graphicCP,
        graphicMutableCP,
        transferCP
    )

    fun of(flags: Int = VkCommandPoolCreate(0).i, queueFamilyIndex: Int): OzCommandPool {
        return OzCommandPool(device,
            device.device.createCommandPool(CommandPoolCreateInfo(flags, queueFamilyIndex))
        )
    }

    val resets = listOf(
        graphicCP, graphicMutableCP
    )
    val waits = listOf(
        transferCP
    )

    suspend fun onRecreateSwapchain(job: CompletableJob): List<Job> {
        waits.forEach {
            it.wait(job)
        }
        return resets.map { it.wait_reset_im(job) }
    }


    fun destroy() {
        cps.forEach {
            it.destroy()
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }



}