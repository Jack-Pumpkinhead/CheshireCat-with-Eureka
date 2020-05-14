package vulkan.buffer

import glm_.L
import kool.*
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.util.vma.Vma
import org.lwjgl.util.vma.VmaAllocationCreateInfo
import org.lwjgl.util.vma.VmaAllocatorCreateInfo
import org.lwjgl.util.vma.VmaVulkanFunctions
import org.lwjgl.vulkan.VkBufferCreateInfo
import vkk.*
import vulkan.OzDevice
import vulkan.OzInstance
import vulkan.OzPhysicalDevice
import vulkan.OzVulkan
import vulkan.drawing.VMABuffer

class OzVMA(
    val ozInstance: OzInstance,
    val ozPhysicalDevice: OzPhysicalDevice,
    val ozDevice: OzDevice
) {

    val pAllocator: Long

    fun create(
        bytes: Long,
        bufferUsage: Int,
        memoryProperty: Int,
        memoryProperty_prefered: Int = 0,
        vmaMemoryUsage: Int
    ): VMABuffer {
        return Stack {
            val bufferCI = VkBufferCreateInfo.mallocStack(it).set(
                VkStructureType.BUFFER_CREATE_INFO.i,
                NULL,
                VkBufferCreate(0).i,
                bytes,
                bufferUsage,
                VkSharingMode.EXCLUSIVE.i,
                null
            )
            val vmaAllocationCI = VmaAllocationCreateInfo.mallocStack(it).set(
                0,
                vmaMemoryUsage,
                memoryProperty,
                memoryProperty_prefered,
                0,  // vma library internally queries Vulkan for memory types supported for that buffer or image (function vkGetBufferMemoryRequirements()) and uses only one of these types.
                NULL,
                NULL
            )
            val allocationP = PointerBuffer(1)
            val buffer = it.callocLong(1)
            Vma.vmaCreateBuffer(pAllocator, bufferCI, vmaAllocationCI, buffer, allocationP, null)
            return@Stack VMABuffer(this.pAllocator, buffer[0], allocationP[0])
        }
    }


    fun of_staging(bytes: Int) = create(
        bytes.L,
        VkBufferUsage.TRANSFER_SRC_BIT.i,
        VkMemoryProperty.HOST_VISIBLE_BIT.i,
        VkMemoryProperty.HOST_COHERENT_BIT.i,
        Vma.VMA_MEMORY_USAGE_CPU_ONLY
    )
    fun of_staging_vertex(bytes: Int) = create(
        bytes.L,
        VkBufferUsage.TRANSFER_SRC_BIT.or(VkBufferUsage.VERTEX_BUFFER_BIT),
        VkMemoryProperty.HOST_VISIBLE_BIT.i,
        VkMemoryProperty.HOST_COHERENT_BIT.i,
        Vma.VMA_MEMORY_USAGE_CPU_ONLY
    )

    fun of_staging_index(bytes: Int) = create(
        bytes.L,
        VkBufferUsage.TRANSFER_SRC_BIT.or(VkBufferUsage.INDEX_BUFFER_BIT),
        VkMemoryProperty.HOST_VISIBLE_BIT.i,
        VkMemoryProperty.HOST_COHERENT_BIT.i,
        Vma.VMA_MEMORY_USAGE_CPU_ONLY
    )


    fun of_VertexBuffer_device_local(bytes: Int): VMABuffer = create(
        bytes.L,
        VkBufferUsage.TRANSFER_DST_BIT.or(VkBufferUsage.VERTEX_BUFFER_BIT),
        VkMemoryProperty.DEVICE_LOCAL_BIT.i,
        vmaMemoryUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
    )
    fun of_IndexBuffer_device_local(bytes: Int): VMABuffer = create(
        bytes.L,
        VkBufferUsage.TRANSFER_DST_BIT.or(VkBufferUsage.INDEX_BUFFER_BIT),
        VkMemoryProperty.DEVICE_LOCAL_BIT.i,
        vmaMemoryUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
    )

    fun of_uniform(bytes: Int) = create(
        bytes = bytes.L,
        bufferUsage = VkBufferUsage.UNIFORM_BUFFER_BIT.i,
        memoryProperty = VkMemoryProperty.HOST_VISIBLE_BIT.i,
        memoryProperty_prefered = VkMemoryProperty.HOST_COHERENT_BIT.i,
        vmaMemoryUsage = Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
    )

    //manual flush
    fun of_uniform_mf(bytes: Int) = create(
        bytes = bytes.L,
        bufferUsage = VkBufferUsage.UNIFORM_BUFFER_BIT.i,
        memoryProperty = VkMemoryProperty.HOST_VISIBLE_BIT.i,
        vmaMemoryUsage = Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
    )







    init {
        pAllocator = Stack {
            val buffer = it.calloc(VmaAllocatorCreateInfo.SIZEOF)
            memPutAddress(buffer.adr + VmaAllocatorCreateInfo.DEVICE, ozDevice.device.address())
            memPutAddress(buffer.adr + VmaAllocatorCreateInfo.PHYSICALDEVICE, ozPhysicalDevice.pd.address())
            val vulkanFunctions = VmaVulkanFunctions.mallocStack(it).set(
                ozInstance.instance.capabilities.vkGetPhysicalDeviceProperties,
                ozInstance.instance.capabilities.vkGetPhysicalDeviceMemoryProperties,
                ozDevice.device.capabilities.vkAllocateMemory,
                ozDevice.device.capabilities.vkFreeMemory,
                ozDevice.device.capabilities.vkMapMemory,
                ozDevice.device.capabilities.vkUnmapMemory,
                ozDevice.device.capabilities.vkFlushMappedMemoryRanges,
                ozDevice.device.capabilities.vkInvalidateMappedMemoryRanges,
                ozDevice.device.capabilities.vkBindBufferMemory,
                ozDevice.device.capabilities.vkBindImageMemory,
                ozDevice.device.capabilities.vkGetBufferMemoryRequirements,
                ozDevice.device.capabilities.vkGetImageMemoryRequirements,
                ozDevice.device.capabilities.vkCreateBuffer,
                ozDevice.device.capabilities.vkDestroyBuffer,
                ozDevice.device.capabilities.vkCreateImage,
                ozDevice.device.capabilities.vkDestroyImage,
                ozDevice.device.capabilities.vkCmdCopyBuffer,
                ozDevice.device.capabilities.vkGetBufferMemoryRequirements2KHR,
                ozDevice.device.capabilities.vkGetImageMemoryRequirements2KHR,
                ozDevice.device.capabilities.vkBindBufferMemory2KHR,
                ozDevice.device.capabilities.vkBindImageMemory2KHR
            )
            memPutAddress(buffer.adr + VmaAllocatorCreateInfo.PVULKANFUNCTIONS, vulkanFunctions.address())

            val pointer = PointerBuffer(1)
            Vma.vmaCreateAllocator(VmaAllocatorCreateInfo(buffer), pointer)
            pointer
        }.get(0)
    }

    fun destroy() {
        Vma.vmaDestroyAllocator(pAllocator)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

}