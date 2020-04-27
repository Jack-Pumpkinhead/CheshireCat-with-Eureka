package vulkan

import kool.BYTES
import mu.KotlinLogging
import vkk.VkFormat
import vkk.VkVertexInputRate
import vkk.vk10.structs.VertexInputAttributeDescription
import vkk.vk10.structs.VertexInputBindingDescription

class OzVertexInput {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val bindingDescription = VertexInputBindingDescription(
        binding = 0,
        stride = (3 + 3) * Float.BYTES,
        inputRate = VkVertexInputRate.VERTEX
    )
    val posAD = VertexInputAttributeDescription(
        binding = 0,
        location = 0,
        format = VkFormat.R32G32B32_SFLOAT,
        offset = 0
    )
    val colorAD = VertexInputAttributeDescription(
        binding = 0,
        location = 1,
        format = VkFormat.R32G32B32_SFLOAT,
        offset = 3 * Float.BYTES
    )

}