package vulkan.drawing

import kool.Stack
import kool.adr
import kool.remSize
import mu.KotlinLogging
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.vma.Vma
import vkk.entities.VkBuffer
import vkk.entities.VkDeviceSize
import vkk.entities.VkDeviceSize.Companion.WHOLE_SIZE
import vkk.memCopy
import java.nio.Buffer

class VMABuffer(val pAllocator: Long, val pBuffer: Long, val pAllocation: Long) {

    val vkBuffer = VkBuffer(pBuffer)

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

    fun flush(offset: Long = 0, size: Long = WHOLE_SIZE.L) {
        Vma.vmaFlushAllocation(pAllocator, pAllocation, offset, size)
    }


    fun destroy() {
        unmap()
        Vma.vmaDestroyBuffer(pAllocator, pBuffer, pAllocation)
    }



}