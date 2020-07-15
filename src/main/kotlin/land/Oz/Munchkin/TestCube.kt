package land.Oz.Munchkin

import game.Primitive
import game.main.Univ
import math.matrix.Model
import math.vector.halfPI
import vulkan.pipelines.PipelineTextured

/**
 * Created by CowardlyLion on 2020/7/3 16:40
 */
class TestCube(univ: Univ) : Primitive(univ) {

    lateinit var cube: PipelineTextured.ObjStatic
    lateinit var model0: Model


    val crafting_table = univ.emeralds.get("crafting_table.dae")!!
    override suspend fun initialize() {
        val node = crafting_table.structure.nodes().find {
            it.name == "Cube"
        }!!

        val mesh = node.meshes[0]

        model0 = univ.vulkan.layoutMVP.model.fetch()
        model0.rot.x = halfPI

        cube = PipelineTextured.ObjStatic(
            univ = univ,
            texIndex = 0,
            matrixIndex = model0.index,
            indices = mesh.indices.toIntArray(),
            pos_texCoord = join(mesh.vertex, mesh.texCoord(0))
        )
        univ.frameLoop.drawCmds3.withLockS { cmds ->
            cmds.add(cube.recorder)
        }
    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        model0.update()
    }

    fun join(pos: List<Float>, tex: List<Float>): FloatArray {
        val arr = mutableListOf<Float>()
        var i = 0
        var j = 0
        while (i < pos.size) {
            arr += pos[i]
            arr += pos[i + 1]
            arr += pos[i + 2]
            arr += tex[j]
            arr += tex[j + 1]
            i += 3
            j += 2
        }
        return arr.toFloatArray()
    }
}