package vulkan.image

import org.lwjgl.util.vma.Vma
import vkk.entities.VkImage
import vulkan.buffer.VmaAllocation

/**
 * Created by CowardlyLion on 2020/5/25 17:42
 */
class VmaImage(val pAllocator: Long, val pImage: Long, val pAllocation: Long) {

    val vkImage = VkImage(pImage)
    val memory = VmaAllocation(pAllocator, pAllocation)

    fun destroy() {
        memory.unmap()
        Vma.vmaDestroyImage(pAllocator, pImage, pAllocation)
    }


}