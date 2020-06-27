package vulkan.buffer

import org.lwjgl.util.vma.Vma
import vkk.entities.VkBuffer

class VmaBuffer(val pAllocator: Long, val pBuffer: Long, val pAllocation: Long) {

    val vkBuffer = VkBuffer(pBuffer)
    val memory = VmaAllocation(pAllocator, pAllocation)



    fun destroy() {
        memory.unmap()
        Vma.vmaDestroyBuffer(pAllocator, pBuffer, pAllocation)
    }



}