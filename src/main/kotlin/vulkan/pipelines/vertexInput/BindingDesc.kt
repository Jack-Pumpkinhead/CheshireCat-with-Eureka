package vulkan.pipelines.vertexInput

import kool.BYTES
import vkk.VkVertexInputRate
import vkk.vk10.structs.VertexInputBindingDescription

/**
 * Created by CowardlyLion on 2020/5/30 23:22
 */

val bingding06V = VertexInputBindingDescription(
    binding = 0,
    stride = 6 * Float.BYTES,
    inputRate = VkVertexInputRate.VERTEX
)