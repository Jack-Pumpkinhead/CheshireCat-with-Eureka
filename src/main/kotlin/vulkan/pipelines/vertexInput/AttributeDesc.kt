package vulkan.pipelines.vertexInput

import kool.BYTES
import vkk.VkFormat
import vkk.vk10.structs.VertexInputAttributeDescription

/**
 * Created by CowardlyLion on 2020/5/30 23:22
 */

val pos0030 = VertexInputAttributeDescription(
    binding = 0,
    location = 0,
    format = VkFormat.R32G32B32_SFLOAT,
    offset = 0
)
val pos0040 = VertexInputAttributeDescription(
    binding = 0,
    location = 0,
    format = VkFormat.R32G32B32A32_SFLOAT,
    offset = 0
)


val color0133 = VertexInputAttributeDescription(
    binding = 0,
    location = 1,
    format = VkFormat.R32G32B32_SFLOAT,
    offset = 3 * Float.BYTES
)

val coordinate0124 = VertexInputAttributeDescription(
    binding = 0,
    location = 1,
    format = VkFormat.R32G32_SFLOAT,
    offset = 4 * Float.BYTES
)
