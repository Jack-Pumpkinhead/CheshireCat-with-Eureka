package vulkan.image

import game.input.SpringInput
import vkk.entities.VkSampler
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

    val list = mutableListOf<OzImage>()

    var index = 0
    fun put(path: String, sampler: VkSampler): Int {
        list += OzImage(
            vma, device, commandPools, queues, ozImageViews, springInput,
            path, sampler
        )
        return index++
    }

    val crafting_table = put("textures\\crafting_table.png", samplers.samplerNearest)
    val UVA = put("textures\\UVA.png", samplers.sampler)
    val UVB = put("textures\\UVB.png", samplers.sampler)
    val Icosphere_blue = put("textures\\Icosphere_blue.png", samplers.sampler)
    val Icosphere_green = put("textures\\Icosphere_green.png", samplers.sampler)
    val Icosphere_red = put("textures\\Icosphere_red.png", samplers.sampler)
    val Icosphere_yellow = put("textures\\Icosphere_yellow.png", samplers.sampler)
    val noise = put("textures\\noise.png", samplers.sampler)



    fun destroy() {
        list.forEach { it.destroy() }
    }
}