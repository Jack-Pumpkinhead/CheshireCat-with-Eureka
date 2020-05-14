package vulkan.pipelines.vertexInput

import kool.BYTES
import mu.KotlinLogging
import vkk.VkFormat
import vkk.VkVertexInputRate
import vkk.vk10.structs.PipelineVertexInputStateCreateInfo
import vkk.vk10.structs.VertexInputAttributeDescription
import vkk.vk10.structs.VertexInputBindingDescription

class OzVertexInput_p3c3 {

    companion object {

        fun bytes(vertexCount: Int) = (3 + 3) * Float.BYTES * vertexCount

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

    val inputState = PipelineVertexInputStateCreateInfo(
        vertexBindingDescriptions = arrayOf(bindingDescription),
        vertexAttributeDescriptions = arrayOf(posAD, colorAD)
    )

}