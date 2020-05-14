package vulkan.buffer

import kool.Stack
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.util.vma.Vma

class OzVMABuffer(val vma: OzVMA, val pBuffer: Long, val pAllocation: Long) {


    var mapped: Long = NULL


    fun map(): Long {
        if (mapped == NULL) {
            Stack {
                val pMap = it.mallocPointer(1)
                Vma.vmaMapMemory(vma.pAllocator, pAllocation, pMap)
                mapped = pMap.get(0)
            }
        }
        return mapped
    }
    fun unmap() {
        if (mapped != NULL) {
            mapped = NULL
            Vma.vmaUnmapMemory(vma.pAllocator, pAllocation)
        }
    }

    inline fun withMap(action: (Long) -> Unit) {
        action(map())
        unmap()
    }

    fun flushMapped(offset: Long, size: Long) {
        Vma.vmaFlushAllocation(vma.pAllocator, pAllocation, offset, size)
    }


    fun destroy() {
        unmap()
        Vma.vmaDestroyBuffer(vma.pAllocator, pBuffer, pAllocation)
    }




}