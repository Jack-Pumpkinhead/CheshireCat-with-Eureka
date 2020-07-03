package vulkan.pipelines.vertexInput

import glm_.vec2.Vec2
import glm_.vec3.Vec3
import kool.BYTES

/**
 * Created by CowardlyLion on 2020/7/3 14:28
 */

class VertexP3C3(val pos: Vec3, val color: Vec3) {
    companion object {
        fun bytes(vertexCount: Int) = (3 + 3) * Float.BYTES * vertexCount
    }
}
class VertexP3C3T2(val pos: Vec3, val color: Vec3,val texCoord: Vec2) {
    companion object {
        fun bytes(vertexCount: Int) = (3 + 3 + 2) * Float.BYTES * vertexCount
    }
}
class VertexP3T2(val pos: Vec3, val texCoord: Vec2) {
    companion object {
        fun bytes(vertexCount: Int) = (3 + 2) * Float.BYTES * vertexCount
    }
}
