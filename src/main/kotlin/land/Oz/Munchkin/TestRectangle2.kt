package land.Oz.Munchkin

import game.Primitive
import game.main.Univ
import math.matrix.Model
import vulkan.drawing.BuildInData
import vulkan.pipelines.PipelineBasic2

/**
 * Created by CowardlyLion on 2020/6/29 11:52
 */
class TestRectangle2(univ: Univ): Primitive(univ) {
    lateinit var obj0: PipelineBasic2.ObjStatic
    lateinit var obj1: PipelineBasic2.ObjStatic
    lateinit var model0: Model
    lateinit var model1: Model

    init {
//        instantiate = false
//        active = false
    }

    override suspend fun initialize() {
        Univ.logger.info {
            "Testing: ${javaClass.name}"
        }
        model0 = univ.vulkan.layoutMVP.model.fetch()
        model1 = univ.vulkan.layoutMVP.model.fetch()

        model0.scale = 10F
//        model0.scale = 10F

        obj0 = PipelineBasic2.ObjStatic(
            univ.vulkan.vma,
            univ.vulkan.copybuffer,
            univ.vulkan.graphicPipelines.hellomvp4,
            univ.vulkan.layoutMVP,
            vert_color = BuildInData.vcRect,
            indices = BuildInData.iRect,
            matrixIndex = model0.index
        )
        obj1 = PipelineBasic2.ObjStatic(
            univ.vulkan.vma,
            univ.vulkan.copybuffer,
            univ.vulkan.graphicPipelines.hellomvp4,
            univ.vulkan.layoutMVP,
            vert_color = BuildInData.vcRect,
            indices = BuildInData.iRect,
            matrixIndex = model1.index
        )

        /*val mat = Mat4().translateAssign(Vec3(0f, 0f, 0f))
        univ.vulkan.dms.dms.matrices.withLock {
            it += Mat4()
            it += mat
            it += Mat4()
            it += Mat4().translateAssign(Vec3(0f, 0f, 0f))
        }*/

        univ.frameLoop.drawCmds3.withLockS { cmds ->
            cmds.add(obj1.recorder)
            cmds.add(obj0.recorder)
        }
    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        model0.update()
        model1.update()
    }
}