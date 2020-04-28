package vulkan.drawing

import kool.Stack
import mu.KotlinLogging
import org.lwjgl.demo.vulkan.NvRayTracingExample
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.util.vma.Vma
import vulkan.OzVulkan
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class OzVMABuffer(ozVulkan: OzVulkan, val vma: OzVMA, val pBuffer: Long, val pAllocation: Long) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

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



    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(vma::destroy, this::destroy)
    }


    fun destroy() {
        unmap()
        Vma.vmaDestroyBuffer(vma.pAllocator, pBuffer, pAllocation)
    }




}