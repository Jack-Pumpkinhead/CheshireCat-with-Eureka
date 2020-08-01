package land.Oz.Munchkin

import game.Primitive
import game.join
import game.main.Univ
import math.vector.halfPI
import vulkan.buffer.makeDataVI
import vulkan.command.BindMVPTexture
import vulkan.command.bindSet
import vulkan.drawing.DataVI

/**
 * Created by CowardlyLion on 2020/7/22 13:40
 */
class TestCube2(univ: Univ) : Primitive(univ) {

    lateinit var data: DataVI
    lateinit var cube: BindMVPTexture

    override suspend fun initialize() {
        val crafting_table = univ.emeralds.get("crafting_table.dae")!!
        val node = crafting_table.structure.nodes().find { it.name == "Cube" }!!
        val mesh = node.meshes[0]

        val model = univ.vulkan.descriptorSets.mvp.model.fetchModel()
        model.rot.x = halfPI
        model.update()


        data = univ.makeDataVI(
            join(mesh.vertex, mesh.texCoord(0)),
            mesh.indices.toIntArray()
        )
        cube = univ.bindSet(model, univ.loader.textureSets.crafting_table)
        data.descriptors.assign(cube)

        val dataIn = univ.vulkan.graphicPipelines.singleTexture.obj.arr.assign(data)

        univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
            univ.vulkan.graphicPipelines.singleTexture.obj.arr.assign(data)
        }
    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {

    }

    override suspend fun destroy() {
        cube.model.destroy()
    }
}