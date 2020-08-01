package land.Oz.Quadling.knot

import game.Primitive
import game.main.Univ
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.withLock
import math.matrix.InArrModel
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

    init {
        instantiate = false
    }

    lateinit var knot: Knot
    lateinit var data: DataVI
    lateinit var data_cross: DataVI
    lateinit var data_near: DataVI

    lateinit var model: InArrModel
    lateinit var bind: BindMVP


    val fpv = univ.matrices.fpv
    val mvp = univ.vulkan.descriptorSets.mvp

    var drawing = true
    var smooth = false
    override suspend fun initialize() {
        knot = Knot(Vec3(-10, 0, 0), Vec3())
        data = univ.makeDataVI_Dynamic(knot.vertexData(), knot.indexData())
        data_cross = univ.makeDataVI_Dynamic(knot.vertexData_Crossing(), knot.indexData_Crossing())
        data_near = univ.makeDataVI_Dynamic(knot.nearPointV(), knot.nearPointI())

        model = univ.fetchModel()
        bind = univ.bindSet(model)
        data.descriptors.assign(bind)
        data_cross.descriptors.assign(bind)
        data_near.descriptors.assign(bind)

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
            if (key == Key.C) {
                knot.findCrossing()
            }

        }

        univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
//        univ.vulkan.graphicPipelines.line_thin.obj.arr.assign(data_cross)
        univ.vulkan.graphicPipelines.line.obj.arr.assign(data_cross)
        univ.vulkan.graphicPipelines.line.obj.arr.assign(data_near)

        univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
            univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
//            univ.vulkan.graphicPipelines.line_thin.obj.arr.assign(data_cross)
            univ.vulkan.graphicPipelines.line.obj.arr.assign(data_cross)
            univ.vulkan.graphicPipelines.line.obj.arr.assign(data_near)
        }

        univ.events.perSecond.subscribe { sec ->
            knot.mutex.withLock {
                Univ.logger.info {
                    "size ${knot.points.size}}"
                }
                Univ.logger.info {
                    "A ${knot.crossingA.size}: ${knot.crossingA.joinToString()}"
                }
                Univ.logger.info {
                    "B ${knot.crossingB.size}: ${knot.crossingB.joinToString()}"
                }
                Univ.logger.info {
//                    "near ${knot.nearPoint.size}: ${knot.nearPoint.joinToString { it.toString() }}"
                    "near ${knot.nearPoint.size}"
                }

                /*Univ.logger.info {
                    "t0: ${knot.t0}"
                }
                Univ.logger.info {
                    "t1: ${knot.t1}"
                }*/


            }


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
        data_cross.vertex.arr = knot.vertexData_Crossing()
        data_cross.index.arr = knot.indexData_Crossing()
        data_cross.vertex.reload_Dynamic()
        data_cross.index.reload_Dynamic()

        data_near.vertex.arr = knot.nearPointV()
        data_near.index.arr = knot.nearPointI()
        data_near.vertex.reload_Dynamic()
        data_near.index.reload_Dynamic()

        model.update()
    }

    override suspend fun destroy() {
        data.destroy()
        data_cross.destroy()
        data_near.destroy()
        model.destroy()
    }
}