package land.Oz.Munchkin

import game.Primitive
import game.main.Univ
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.withLock
import math.matrix.InArrModel
import physics.Knot
import uno.glfw.Key
import vulkan.buffer.makeDataVI_Dynamic
import vulkan.command.BindMVP
import vulkan.command.bindSet
import vulkan.drawing.DataVI
import vulkan.pipelines.descriptor.fetchModel

/**
 * Created by CowardlyLion on 2020/7/23 14:42
 */
class TestKnot(univ: Univ): Primitive(univ) {

    lateinit var knot: Knot
    lateinit var data: DataVI

    lateinit var model: InArrModel
    lateinit var bind: BindMVP


    val fpv = univ.matrices.fpv
    val mvp = univ.vulkan.descriptorSets.mvp

    var drawing = true
    var smooth = false
    override suspend fun initialize() {
        knot = Knot(Vec3(-10, 0, 0), Vec3())
        data = univ.makeDataVI_Dynamic(knot.vertexData(), knot.indexData())
        model = univ.fetchModel()
        bind = univ.bindSet(model)
        data.descriptors.assign(bind)

        univ.events.keyPress.subscribe { (key, mods) ->
            if (key == Key.K) {
                knot = Knot(fpv.forward(1F), fpv.direction())
            }
            if (key == Key.R) {
                drawing = !drawing
            }
            if (key == Key.T) {
                smooth = !smooth
            }
            if (key == Key.Y) {
                knot.angle = !knot.angle
            }

        }

        univ.vulkan.graphicPipelines.line.obj.arr.assign(data)

        univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
            univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
        }
    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        if (drawing) {
            knot.tryGenerate(
                fpv.forward(1F)
            )
        }
        if (smooth) {
            knot.update()
        }
    }

    override suspend fun updateBuffer(imageIndex: Int) {
        data.vertex.arr = knot.vertexData()
        data.index.arr = knot.indexData()
        data.vertex.reload_Dynamic()
        data.index.reload_Dynamic()
        model.update()
    }

    override suspend fun destroy() {
        data.destroy()
        model.destroy()
    }
}