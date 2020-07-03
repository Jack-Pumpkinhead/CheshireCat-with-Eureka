package vulkan.image

import game.input.SpringInput
import vulkan.OzCommandPools
import vulkan.OzDevice
import vulkan.OzImageViews
import vulkan.OzQueues
import vulkan.buffer.OzVMA

/**
 * Created by CowardlyLion on 2020/5/30 23:07
 */
class OzImages(
    val vma: OzVMA,
    val device: OzDevice,
    val commandPools: OzCommandPools,
    val queues: OzQueues,
    val ozImageViews: OzImageViews,
    val springInput: SpringInput,
    val samplers: Samplers
) {

    val image = OzImage(
        vma, device, commandPools, queues, ozImageViews, springInput,
        "textures\\crafting_table.png", samplers.samplerNearest
    )

    val list = listOf(image)



    fun destroy() {
        image.destroy()
    }
}