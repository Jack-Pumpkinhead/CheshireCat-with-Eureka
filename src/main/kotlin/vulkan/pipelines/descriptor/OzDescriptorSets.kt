package vulkan.pipelines.descriptor

import kotlinx.coroutines.runBlocking
import vulkan.OzDescriptorPools
import vulkan.OzDevice
import vulkan.OzPhysicalDevice
import vulkan.buffer.OzVMA
import vulkan.image.OzImages
import vulkan.image.Samplers
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.util.SurfaceSwapchainSupport

/**
 * Created by CowardlyLion on 2020/7/21 11:57
 */
class OzDescriptorSets(
    val vma: OzVMA,
    val descriptorPools: OzDescriptorPools,
    val device: OzDevice,
    val physicalDevice: OzPhysicalDevice,
    val sss: SurfaceSwapchainSupport,
    val pipelineLayouts: OzPipelineLayouts,
    val setLayouts: SetLayouts
) {
    val mvp = SetMVP(
        vma, descriptorPools, device, physicalDevice, sss, pipelineLayouts
    )
    val singleTexture = SingleTextureSets(device, setLayouts, descriptorPools)


    fun destroy() {
        runBlocking {
            mvp.destroy()
            singleTexture.clear()
        }
    }



}