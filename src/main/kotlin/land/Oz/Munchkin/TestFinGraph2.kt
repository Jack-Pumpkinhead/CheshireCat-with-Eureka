package land.Oz.Munchkin

import game.Primitive
import game.main.Univ
import glm_.vec3.Vec3
import math.graph.randomFinGraph
import math.matrix.InArrModel
import physics.VisualFinGraph
import vulkan.buffer.makeDataVI
import vulkan.buffer.makeDataVI_Dynamic
import vulkan.command.BindMVP
import vulkan.command.bindSet
import vulkan.drawing.DataVI
import vulkan.pipelines.descriptor.fetchModel

/**
 * Created by CowardlyLion on 2020/7/22 16:33
 */
class TestFinGraph2(univ: Univ): Primitive(univ) {

    val graph = randomFinGraph(50, 37)
    val vGraph = VisualFinGraph(graph, pivotEnabled = true, pivot = Vec3(0, -5, 0))
    lateinit var data: DataVI

    lateinit var model: InArrModel
    lateinit var bind: BindMVP

    override suspend fun initialize() {
        data = univ.makeDataVI_Dynamic(vGraph.vertexData(), vGraph.indexData())

        model = univ.fetchModel()
        bind = univ.bindSet(model)
        data.descriptors.assign(bind)

        univ.vulkan.graphicPipelines.line.obj.arr.assign(data)

        univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
            univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
        }
    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        vGraph.update(tick, timemillis)

        //开销过大 + 不同步
        data.vertex.arr = vGraph.vertexData()
        data.index.arr = vGraph.indexData()
        data.vertex.reload_Dynamic()
        data.index.reload_Dynamic()
        model.update()
    }

    override suspend fun destroy() {
        model.destroy()
    }
}