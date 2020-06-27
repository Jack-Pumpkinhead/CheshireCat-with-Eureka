package vulkan.pipelines.vertexInput

import glm_.vec3.Vec3
import kool.BYTES
import vkk.vk10.structs.PipelineVertexInputStateCreateInfo

class Vertex_p3c3(val pos: Vec3, val color: Vec3) {

    companion object {

        fun bytes(vertexCount: Int) = (3 + 3) * Float.BYTES * vertexCount
        val inputState = PipelineVertexInputStateCreateInfo(
            vertexBindingDescriptions = arrayOf(bingding06V),
            vertexAttributeDescriptions = arrayOf(pos0030, color0133)
        )

    }







}