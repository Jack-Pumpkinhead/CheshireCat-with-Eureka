package vulkan.pipelines.vertexInput

import kool.BYTES
import vkk.VkFormat
import vkk.VkVertexInputRate
import vkk.vk10.structs.PipelineVertexInputStateCreateInfo
import vkk.vk10.structs.VertexInputAttributeDescription
import vkk.vk10.structs.VertexInputBindingDescription

/**
 * Created by CowardlyLion on 2020/7/3 11:15
 */
class VertexInput {
    companion object {
        val P3C3 = stateCI(
            intArrayOf(3, 3) to intArrayOf(0, 1)
        )
        val P3T2 = stateCI(
            intArrayOf(3, 2) to intArrayOf(0, 2)
        )
        val P3C3T2 = stateCI(
            intArrayOf(3, 3, 2) to intArrayOf(0, 1, 2)
        )


        fun bindingDesc_Vert(binding: Int, dim: Int): VertexInputBindingDescription {
            return VertexInputBindingDescription(
                binding = binding,
                stride = dim * Float.BYTES,
                inputRate = VkVertexInputRate.VERTEX
            )
        }
        fun attributeDesc(binding: Int, dim: IntArray): Array<VertexInputAttributeDescription> {
            val result = mutableListOf<VertexInputAttributeDescription>()
            var offset = 0
            for (i in dim.indices) {
                result += VertexInputAttributeDescription(
                    binding = binding,
                    location = i,
                    format = format(dim[i]),
                    offset = offset * Float.BYTES
                )
                offset += dim[i]
            }
            return result.toTypedArray()
        }
        fun attributeDesc(binding: Int, dim: IntArray, loc: IntArray): Array<VertexInputAttributeDescription> {
            val result = mutableListOf<VertexInputAttributeDescription>()
            var offset = 0
            for (i in dim.indices) {
                result += VertexInputAttributeDescription(
                    binding = binding,
                    location = loc[i],
                    format = format(dim[i]),
                    offset = offset * Float.BYTES
                )
                offset += dim[i]
            }
            return result.toTypedArray()
        }


        //vararg
        fun stateCI(vararg bindings: Pair<IntArray, IntArray>): PipelineVertexInputStateCreateInfo {
            val bindingDesc = mutableListOf<VertexInputBindingDescription>()
            val attributeDesc = mutableListOf<VertexInputAttributeDescription>()
            bindings.forEachIndexed { binding, (dim, loc) ->
                var offset = 0
                for (i in dim.indices) {
                    attributeDesc += VertexInputAttributeDescription(
                        binding = binding,
                        location = loc[i],
                        format = format(dim[i]),
                        offset = offset * Float.BYTES   // * BYTES
                    )
                    offset += dim[i]
                }
                bindingDesc += VertexInputBindingDescription(
                    binding = binding,
                    stride = offset * Float.BYTES,  // * BYTES
                    inputRate = VkVertexInputRate.VERTEX
                )
            }
            return PipelineVertexInputStateCreateInfo(
                vertexBindingDescriptions = bindingDesc.toTypedArray(),
                vertexAttributeDescriptions = attributeDesc.toTypedArray()
            )
        }
    }

}

fun format(dim: Int): VkFormat {
    return when (dim) {
        1 -> VkFormat.R32_SFLOAT
        2 -> VkFormat.R32G32_SFLOAT
        3 -> VkFormat.R32G32B32_SFLOAT
        4 -> VkFormat.R32G32B32A32_SFLOAT
        else -> error("Illegal dim: $dim")
    }
}