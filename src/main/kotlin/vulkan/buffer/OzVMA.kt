package vulkan.buffer

import glm_.L
import kool.BYTES
import kool.PointerBuffer
import kool.Stack
import kool.adr
import kotlinx.coroutines.runBlocking
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.system.MemoryUtil.memPutAddress
import org.lwjgl.util.vma.Vma
import org.lwjgl.util.vma.VmaAllocationCreateInfo
import org.lwjgl.util.vma.VmaAllocatorCreateInfo
import org.lwjgl.util.vma.VmaVulkanFunctions
import org.lwjgl.vulkan.VkBufferCreateInfo
import org.lwjgl.vulkan.VkExtent3D
import org.lwjgl.vulkan.VkImageCreateInfo
import vkk.*
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.Extent3D
import vkk.vk10.structs.ImageCreateInfo
import vulkan.OzDevice
import vulkan.OzInstance
import vulkan.OzPhysicalDevice
import vulkan.OzVulkan
import vulkan.command.CopyBuffer
import vulkan.image.VmaImage
import kotlin.math.log2
import kotlin.math.max

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
    ): VmaBuffer = Stack {
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
        return@Stack VmaBuffer(this.pAllocator, buffer[0], allocationP[0])
    }


    fun createBuffer_staging(bytes: Int) = create(
        bytes.L,
        VkBufferUsage.TRANSFER_SRC_BIT.i,
        VkMemoryProperty.HOST_VISIBLE_BIT.i,
        VkMemoryProperty.HOST_COHERENT_BIT.i,
        Vma.VMA_MEMORY_USAGE_CPU_ONLY
    )
    fun createBuffer_vertexStaging(bytes: Int) = create(
        bytes.L,
        VkBufferUsage.TRANSFER_SRC_BIT.or(VkBufferUsage.VERTEX_BUFFER_BIT),
        VkMemoryProperty.HOST_VISIBLE_BIT.i,
        VkMemoryProperty.HOST_COHERENT_BIT.i,
        Vma.VMA_MEMORY_USAGE_CPU_ONLY
    )
    fun vertexBuffer(bytes: Int) = create(
        bytes.L,
        VkBufferUsage.VERTEX_BUFFER_BIT.i,
        VkMemoryProperty.HOST_VISIBLE_BIT.i,
        VkMemoryProperty.HOST_COHERENT_BIT.i,
        Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
    )


    fun createBuffer_indexStaging(bytes: Int) = create(
        bytes.L,
        VkBufferUsage.TRANSFER_SRC_BIT.or(VkBufferUsage.INDEX_BUFFER_BIT),
        VkMemoryProperty.HOST_VISIBLE_BIT.i,
        VkMemoryProperty.HOST_COHERENT_BIT.i,
        Vma.VMA_MEMORY_USAGE_CPU_ONLY
    )
    fun indexBuffer(bytes: Int) = create(
        bytes.L,
        VkBufferUsage.INDEX_BUFFER_BIT.i,
        VkMemoryProperty.HOST_VISIBLE_BIT.i,
        VkMemoryProperty.HOST_COHERENT_BIT.i,
        Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
    )





    fun of_VertexBuffer_device_local(bytes: Int): VmaBuffer = create(
        bytes.L,
        VkBufferUsage.TRANSFER_DST_BIT.or(VkBufferUsage.VERTEX_BUFFER_BIT),
        VkMemoryProperty.DEVICE_LOCAL_BIT.i,
        vmaMemoryUsage = Vma.VMA_MEMORY_USAGE_GPU_ONLY
    )
    fun of_IndexBuffer_device_local(bytes: Int): VmaBuffer = create(
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

    fun of_uniform_manual_flush(bytes: Int) = create(
        bytes = bytes.L,
        bufferUsage = VkBufferUsage.UNIFORM_BUFFER_BIT.i,
        memoryProperty = VkMemoryProperty.HOST_VISIBLE_BIT.i,
        vmaMemoryUsage = Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
    )

    fun createBuffer_imageStaging(bytes: Int) = create(
        bytes = bytes.L,
        bufferUsage = VkBufferUsage.TRANSFER_SRC_BIT.i,
        memoryProperty = VkMemoryProperty.HOST_VISIBLE_BIT.i,
        memoryProperty_prefered = VkMemoryProperty.HOST_COHERENT_BIT.i,
        vmaMemoryUsage = Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
    )


    fun createImage_deviceLocal(
        imageCI: ImageCreateInfo,
        memoryProperty: VkMemoryPropertyFlags = VkMemoryProperty.DEVICE_LOCAL_BIT.i
    ): VmaImage = createImage_deviceLocal(
        imageCI.flags,
        imageCI.imageType.i,
        imageCI.format.i,
        imageCI.extent,
        imageCI.mipLevels,
        imageCI.arrayLayers,
        imageCI.samples.i,
        imageCI.tiling.i,
        imageCI.usage,
        imageCI.sharingMode.i,
        imageCI.queueFamilyIndices,
        imageCI.initialLayout.i,
        imageCI.next,
        memoryProperty
    )
    fun createImage_deviceLocal(
        flags: Int = VkImageCreate(0).i,
        imageType: Int = VkImageType._2D.i,
        format: Int = VkFormat.R8G8B8A8_SRGB.i,
        extent: Extent3D,
        mipLevels: Int = 1,
        arrayLayers: Int = 1,
        samples: Int = VkSampleCount._1_BIT.i,
        tiling: Int = VkImageTiling.OPTIMAL.i,
        usage: Int = VkImageUsage.TRANSFER_DST_BIT.or(VkImageUsage.SAMPLED_BIT),
        sharingMode: Int = VkSharingMode.EXCLUSIVE.i,
        queueFamilyIndices: IntArray? = null,
        initialLayout: Int = VkImageLayout.UNDEFINED.i,
        pNext: Long = 0,
        memoryProperty: VkMemoryPropertyFlags = VkMemoryProperty.DEVICE_LOCAL_BIT.i
    ): VmaImage = Stack {
        val extent3D = VkExtent3D.mallocStack(it).set(extent.width, extent.height, extent.depth)
        val queuefamilyIndices = if (queueFamilyIndices != null) {
            it.mallocInt(queueFamilyIndices.size).put(queueFamilyIndices).flip()
        } else null
        val imageCreateInfo = VkImageCreateInfo.mallocStack(it).set(
            VkStructureType.IMAGE_CREATE_INFO.i,
            pNext,
            flags,
            imageType,
            format,
            extent3D,
            mipLevels,
            arrayLayers,
            samples,
            tiling,
            usage,
            sharingMode,
            queuefamilyIndices,
            initialLayout
        )
        val vmaAllocationCI = VmaAllocationCreateInfo.mallocStack(it).set(
            0,
            Vma.VMA_MEMORY_USAGE_GPU_ONLY,
            memoryProperty,
            0,
            0,  // vma library internally queries Vulkan for memory types supported for that buffer or image (function vkGetBufferMemoryRequirements()) and uses only one of these types.
            NULL,
            NULL
        )
        val pImage = it.callocLong(1)
        val pAllocation = PointerBuffer(1)
        Vma.vmaCreateImage(pAllocator, imageCreateInfo, vmaAllocationCI, pImage, pAllocation, null)
        return@Stack VmaImage(pAllocator, pImage.get(), pAllocation.get())
    }




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