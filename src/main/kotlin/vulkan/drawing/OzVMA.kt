package vulkan.drawing

import glm_.L
import glm_.detail.Random.long
import kool.*
import mu.KotlinLogging
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.util.vma.Vma
import org.lwjgl.util.vma.VmaAllocationCreateInfo
import org.lwjgl.util.vma.VmaAllocatorCreateInfo
import org.lwjgl.util.vma.VmaVulkanFunctions
import org.lwjgl.vulkan.VkBufferCreateInfo
import org.lwjgl.vulkan.VkDevice
import org.lwjgl.vulkan.VkInstance
import org.lwjgl.vulkan.VkInstanceCreateInfo.mallocStack
import org.lwjgl.vulkan.VkPhysicalDevice
import vkk.*
import vulkan.OzDevice
import vulkan.OzPhysicalDevice
import vulkan.OzVulkan

class OzVMA(
    val ozVulkan: OzVulkan,
    val ozPhysicalDevice: OzPhysicalDevice,
    val ozDevice: OzDevice
) {


    companion object {

        val logger = KotlinLogging.logger { }

    }

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






    init {
        pAllocator = Stack {
            val buffer = it.calloc(VmaAllocatorCreateInfo.SIZEOF)
            memPutAddress(buffer.adr + VmaAllocatorCreateInfo.DEVICE, ozDevice.device.address())
            memPutAddress(buffer.adr + VmaAllocatorCreateInfo.PHYSICALDEVICE, ozPhysicalDevice.pd.address())
            val vulkanFunctions = VmaVulkanFunctions.mallocStack(it).set(
                ozVulkan.instance.instance.capabilities.vkGetPhysicalDeviceProperties,
                ozVulkan.instance.instance.capabilities.vkGetPhysicalDeviceMemoryProperties,
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


    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(ozDevice::destroy, this::destroy)
    }

    fun destroy() {
        Vma.vmaDestroyAllocator(pAllocator)
    }

}