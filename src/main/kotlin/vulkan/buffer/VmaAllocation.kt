package vulkan.buffer

import kool.Stack
import kool.adr
import kool.remSize
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.vma.Vma
import vkk.entities.VkDeviceSize
import vkk.memCopy
import java.nio.Buffer

/**
 * Created by CowardlyLion on 2020/5/25 17:45
 */
class VmaAllocation(val pAllocator: Long, val pAllocation: Long) {

    var mapped: Long = MemoryUtil.NULL

    fun map(): Long {
        if (mapped == MemoryUtil.NULL) {
            Stack {
                val pMap = it.mallocPointer(1)
                Vma.vmaMapMemory(pAllocator, pAllocation, pMap)
                mapped = pMap.get(0)
            }
        }
        return mapped
    }
    fun unmap() {
        if (mapped != MemoryUtil.NULL) {
            mapped = MemoryUtil.NULL
            Vma.vmaUnmapMemory(pAllocator, pAllocation)
        }
    }

    inline fun withMap(action: (Long) -> Unit) {
        action(map())
        unmap()
    }

    fun fill(buffer: Buffer) {
        withMap {
            memCopy(buffer.adr, it, VkDeviceSize(buffer.remSize))
        }
//        flushMapped(0,buffer.remSize.L)
    }

    fun flush(offset: Long = 0, size: Long = VkDeviceSize.WHOLE_SIZE.L) {
        Vma.vmaFlushAllocation(pAllocator, pAllocation, offset, size)
    }


}