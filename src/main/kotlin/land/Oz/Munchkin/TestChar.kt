package land.Oz.Munchkin

import game.Primitive
import game.entity.Mesh
import game.getTexturedObj
import game.main.Univ
import glm_.vec3.Vec3
import math.matrix.Model
import vulkan.pipelines.PipelineBasic2
import vulkan.pipelines.PipelineTextured

/**
 * Created by CowardlyLion on 2020/7/7 17:37
 */
class TestChar(univ: Univ) : Primitive(univ) {
    lateinit var cA: PipelineTextured.ObjStatic
    lateinit var cB: PipelineTextured.ObjStatic
    lateinit var cC: PipelineTextured.ObjStatic
    lateinit var mA: Model
    lateinit var mB: Model
    lateinit var mC: Model

    val char = univ.emeralds.get("englishCharacter.dae")!!

    val images = univ.vulkan.images

    override suspend fun initialize() {
        val A = char.find("A")!!
        val B = char.find("B")!!
        val C = char.find("C")!!


        val meshA = A.meshes[0]
        val meshB = B.meshes[0]
        val meshC = C.meshes[0]

//        meshA.mesh.colors



        mA = univ.vulkan.layoutMVP.model.fetch()
        mB = univ.vulkan.layoutMVP.model.fetch()
        mC = univ.vulkan.layoutMVP.model.fetch()
//        model0.rot.x = halfPI
        mA.pos.plusAssign(Vec3(1,0,0))
        mB.pos.plusAssign(Vec3(5,0,0))


        cA = univ.getTexturedObj(meshA, mA.index, images.UVA)
        cB = univ.getTexturedObj(meshB, mB.index, images.UVB)
//        cC = getTexturedObj(meshC, mC.index, images.UVA)


        univ.frameLoop.drawCmds3.withLockS { cmds ->
            cmds.add(cA.recorder)
            cmds.add(cB.recorder)
        }
    }



    override suspend fun gameloop(tick: Long, timemillis: Long) {
        mA.update()
        mB.update()
    }


}