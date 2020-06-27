package vulkan.drawing

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlinx.coroutines.runBlocking
import vulkan.OzVulkan
import vulkan.pipelines.PipelineBasic
import kotlin.random.Random

/**
 * Created by CowardlyLion on 2020/6/17 18:01
 */
class TestPBasic(val ozVulkan: OzVulkan) {
    val obj1 = PipelineBasic.ObjStatic(ozVulkan, BuildInData.vcRect, BuildInData.iRect, 0)
    val obj2 = PipelineBasic.ObjStatic(ozVulkan, BuildInData.vcRect, BuildInData.iRect, 1)

    init {
        runBlocking {
            val mat = Mat4().translateAssign(Vec3(0f, 0f, 0f))
            ozVulkan.dms.dms.matrices.withLock {
                it += Mat4()
                it += mat
                it += Mat4()
                it += Mat4().translateAssign(Vec3(0f, 0f, 0f))
            }
        }
    }


}