package vulkan.buffer

import kool.Stack
import kool.free
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.vma.Vma
import vkk.entities.VkBuffer

class VmaBuffer(val pAllocator: Long, val pBuffer: Long, val pAllocation: Long) {

    val vkBuffer = VkBuffer(pBuffer)
    val memory = VmaAllocation(pAllocator, pAllocation)

    fun fill(arr: FloatArray) {
        if (arr.size < 10000) {
            Stack {
                memory.fill(
                    it.mallocFloat(arr.size).put(arr).flip()
                )
            }
        } else {
            val buffer = MemoryUtil.memAllocFloat(arr.size)
            memory.fill(
                buffer.put(arr).flip()
            )
            buffer.free()
        }
    }
    fun fill(arr: IntArray) {
        if (arr.size < 10000) {
            Stack {
                memory.fill(
                    it.mallocInt(arr.size).put(arr).flip()
                )
            }
        } else {
            val buffer = MemoryUtil.memAllocInt(arr.size)
            memory.fill(
                buffer.put(arr).flip()
            )
            buffer.free()
        }
    }


    fun destroy() {
        memory.unmap()
        Vma.vmaDestroyBuffer(pAllocator, pBuffer, pAllocation)
    }



}