package vulkan.pipelines.vertexInput

import glm_.detail.GlmCoordinateSystem
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kool.BYTES
import vkk.vk10.structs.PipelineVertexInputStateCreateInfo

/**
 * Created by CowardlyLion on 2020/6/1 17:21
 */
class Vertex_p3o2(val pos: Vec4, val coordinate: Vec2) {

    companion object {

        fun bytes(vertexCount: Int) = (3 + 2) * Float.BYTES * vertexCount
        val inputState = PipelineVertexInputStateCreateInfo(
            vertexBindingDescriptions = arrayOf(bingding06V),
            vertexAttributeDescriptions = arrayOf(pos0030, coordinate0124)
        )

    }





}