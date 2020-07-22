package vulkan.command

import gli_.Texture
import vkk.vk10.structs.Extent3D
import vkk.vk10.structs.SubmitInfo
import vulkan.OzCommandPools
import vulkan.OzDevice
import vulkan.OzQueues
import vulkan.buffer.OzVMA
import vulkan.image.VmaImage

class OzCommands(
    val device: OzDevice,
    val commandPools: OzCommandPools,
    val queues: OzQueues,
    val vma: OzVMA
) {

    val copyBuffer = CopyBuffer(commandPools, queues)

    suspend fun fillImage(texture: Texture, image: VmaImage, mipLevels: Int = 1, extent: Extent3D) {
        val deferred = commandPools.graphicMutableCP.allocate(3)
        val buffer = vma.createBuffer_imageStaging(texture.size)
        buffer.memory.fill(texture.data())

        val cbs = deferred.await()
        val trans_dstOptimal = 0
        val copy = 1
        val trans_shaderRead_mipmap = 2

        TransitionImageLayout.transitionImageLayout_tranDst(
            image = image.vkImage,
            cb = cbs[trans_dstOptimal],
            mipLevels = mipLevels
        )

        TransitionImageLayout.copyBufferToImage(
            buffer = buffer.vkBuffer,
            image = image.vkImage,
            width = extent.width,
            height = extent.height,
            cb = cbs[copy]
        )

        TransitionImageLayout.generateMipmaps_toShaderRead(
            image = image.vkImage,
            cb = cbs[trans_shaderRead_mipmap],
            width = extent.width,
            height = extent.height,
            mipLevels = mipLevels
        )
        /*TransitionImageLayout.transitionImageLayout_ShaderRead(
            image = image.vkImage,
            cb = cbs[trans_shaderRead],
            mipLevel = mipLevels
        )*/

        val submitT = queues.graphicQ_2.submit(
            SubmitInfo(
                commandBuffers = cbs
            )
        )
        submitT.await()
        commandPools.graphicMutableCP.free(cbs)
        buffer.destroy()

    }
}