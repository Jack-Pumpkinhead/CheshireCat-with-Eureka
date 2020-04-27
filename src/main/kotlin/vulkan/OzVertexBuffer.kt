package vulkan

import glm_.L
import kool.BYTES
import kool.Stack
import kool.remSize
import mu.KotlinLogging
import org.lwjgl.system.MemoryUtil
import vkk.*
import vkk.entities.VkBuffer
import vkk.entities.VkDeviceMemory
import vkk.entities.VkDeviceSize
import vkk.vk10.*
import vkk.vk10.structs.BufferCreateInfo
import vkk.vk10.structs.CommandBufferAllocateInfo
import vkk.vk10.structs.MemoryAllocateInfo
import vkk.vk10.structs.MemoryRequirements
import java.lang.RuntimeException

class OzVertexBuffer(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val physicalDevice: OzPhysicalDevice,
    val bytes: Int,
    val bufferUsage: Int,
    val memoryProperty: Int
) {

    companion object {

        val logger = KotlinLogging.logger { }

        fun of(device: OzDevice, bytes: Int): OzVertexBuffer = OzVertexBuffer(
            device.ozVulkan, device, device.ozPhysicalDevice,
            bytes,
            VkBufferUsage.VERTEX_BUFFER_BIT.i,
            VkMemoryProperty.HOST_VISIBLE_BIT.or(VkMemoryProperty.HOST_COHERENT_BIT)
        )
        fun ofStaging_staging(device: OzDevice, bytes: Int): OzVertexBuffer = OzVertexBuffer(
            device.ozVulkan, device, device.ozPhysicalDevice,
            bytes,
            VkBufferUsage.TRANSFER_SRC_BIT.i,
            VkMemoryProperty.HOST_VISIBLE_BIT.or(VkMemoryProperty.HOST_COHERENT_BIT)
        )

        fun ofStaging_device_local(device: OzDevice, bytes: Int): OzVertexBuffer = OzVertexBuffer(
            device.ozVulkan,
            device,
            device.ozPhysicalDevice,
            bytes,
            VkBufferUsage.TRANSFER_DST_BIT.or(VkBufferUsage.VERTEX_BUFFER_BIT),
            VkMemoryProperty.DEVICE_LOCAL_BIT.i
        )



    }


    val buffer: VkBuffer = device.device.createBuffer(
        BufferCreateInfo(
            size = VkDeviceSize(bytes),
            usageFlags = bufferUsage,
            sharingMode = VkSharingMode.EXCLUSIVE
        )
    )

    val memoryRequirements: MemoryRequirements = device.device.getBufferMemoryRequirements(buffer)

    val memoryAI: MemoryAllocateInfo = MemoryAllocateInfo(
        allocationSize = memoryRequirements.size,
        memoryTypeIndex = findMemoryType(
            memoryRequirements.memoryTypeBits,
            memoryProperty
        )
    )

    val memory: VkDeviceMemory = device.device.allocateMemory(memoryAI)

    init {
        device.device.bindBufferMemory(
            buffer = buffer,
            memory = memory,
            memoryOffset = VkDeviceSize(0)
        )

    }

    fun findMemoryType(typeFilter: Int, properties: Int): Int {
        val memoryProperties = physicalDevice.pd.memoryProperties
        for ((i, it) in memoryProperties.memoryTypes.withIndex()) {
            val a =
                ((1 shl i) and typeFilter) != 0 //Bit i is set if and only if the memory type i in the VkPhysicalDeviceMemoryProperties structure for the physical device is supported for the resource.
            val b = it.propertyFlags and properties == properties
            if (a && b) return i
        }
        throw RuntimeException("con't find suitable mem type")
    }


    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {

        device.device.destroy(buffer)
        device.device.freeMemory(memory)
    }

}