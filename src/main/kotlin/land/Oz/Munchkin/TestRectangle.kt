package land.Oz.Munchkin

import game.Primitive
import game.main.Univ
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import kotlinx.coroutines.runBlocking
import vulkan.drawing.BuildInData
import vulkan.pipelines.PipelineBasic
import kotlin.random.Random

/**
 * Created by CowardlyLion on 2020/6/27 23:12
 */
class TestRectangle(univ: Univ):Primitive(univ) {

    lateinit var obj1: PipelineBasic.ObjStatic
    lateinit var obj2: PipelineBasic.ObjStatic

    override suspend fun initialize() {
        obj1 = PipelineBasic.ObjStatic(univ.vulkan, BuildInData.vcRect, BuildInData.iRect, 0)
        obj2 = PipelineBasic.ObjStatic(univ.vulkan, BuildInData.vcRect, BuildInData.iRect, 1)

        val mat = Mat4().translateAssign(Vec3(0f, 0f, 0f))
        univ.vulkan.dms.dms.matrices.withLock {
            it += Mat4()
            it += mat
            it += Mat4()
            it += Mat4().translateAssign(Vec3(0f, 0f, 0f))
        }

        univ.frameLoop.drawCmds2.withLockS { cmds ->
            cmds.add(obj2.recorder)
            cmds.add(obj1.recorder)
        }
    }

    var pos = Vec3(0,0,0.2f)
    //    var pos_ = Vec3(0,0,0f)
    var pos2 = Vec3(0,0,0.4f)
    //    var pos2_ = Vec3(0,0,0f)
    override suspend fun gameloop(tick: Long, timemillis: Long) {
        univ.vulkan.dms.dms.matrices.withLock { mats ->

            fun rnd() = (Random.nextFloat() - 0.5f) / 1000

//                pos.plusAssign(0,rnd(),0)
            pos.plusAssign(0, 0.003f, 0)
//                pos_.plusAssign(pos)
//                pos2.plusAssign(rnd(),0,0)
            pos2.plusAssign(0.003f, 0, -0.001f)
//                pos2_.plusAssign(pos)
            mats[1] = Mat4().translateAssign(
                pos
            )
            mats[2] = Mat4().translateAssign(
                pos2
            )

        }
    }
}