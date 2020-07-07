package land.Oz.Munchkin

import game.Primitive
import game.main.Univ
import glm_.vec3.Vec3
import math.graph.randomFinGraph
import math.matrix.Model
import physics.VisualFinGraph
import vulkan.drawing.ObjDynamic

/**
 * Created by CowardlyLion on 2020/7/7 12:18
 */
class TestFinGraph(univ: Univ): Primitive(univ) {
//    val graph = randomFinGraph(23, 37)
    val graph = randomFinGraph(50, 37)
    val vGraph = VisualFinGraph(graph, pivotEnabled = true, pivot = Vec3(0, -5, 0))

    lateinit var drawable: ObjDynamic
    lateinit var model: Model

    override suspend fun initialize() {
        model = univ.vulkan.layoutMVP.model.fetch()
        drawable = ObjDynamic(
            univ.vulkan.vma,
            univ.vulkan.layoutMVP,
            univ.vulkan.pipelineLayouts,
            pipeline = univ.vulkan.graphicPipelines.helloline.graphicsPipeline,
            vert_color_etc = vGraph.vertexData(),
            indices = vGraph.indexData(),
            matrixIndex = model.index
        )
        val index = univ.frameLoop.dynamicObjs.assign(drawable)
    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        vGraph.update(tick, timemillis)
        drawable.update(vGraph.vertexData())
        model.update()

    }

    override suspend fun destroy() {

    }
}