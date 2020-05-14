package vulkan

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import vkk.VkDescriptorPoolCreate
import vkk.VkDescriptorType
import vkk.vk10.createDescriptorPool
import vkk.vk10.structs.DescriptorPoolCreateInfo
import vkk.vk10.structs.DescriptorPoolSize
import vulkan.concurrent.OzDescriptorPool
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/5/9 17:38
 */
class OzDescriptorPools(val device: OzDevice, surfaceSupport: SurfaceSwapchainSupport) {

    val pool: OzDescriptorPool

    init {
        val descriptorPool = device.device.createDescriptorPool(
            DescriptorPoolCreateInfo(
                flags = VkDescriptorPoolCreate(0).i,
                poolSizes = arrayOf(
                    DescriptorPoolSize(
                        type = VkDescriptorType.UNIFORM_BUFFER,
                        descriptorCount = surfaceSupport.imageCount * 1 //maybe more
                    ),
                    DescriptorPoolSize(
                        type = VkDescriptorType.UNIFORM_BUFFER_DYNAMIC,
                        descriptorCount = surfaceSupport.imageCount
                    )
                ),
                maxSets = surfaceSupport.imageCount * 1 + 1
            )
        )
        pool = OzDescriptorPool(device, descriptorPool)

    }

    val pools = listOf(pool)


    suspend fun onRecreateSwapchain(job: Job): List<CompletableJob> {
        return pools.map {
            it.wait_reset_im(job)
        }
    }

    fun destroy() {
        pools.forEach {
            it.destroy()
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }


}