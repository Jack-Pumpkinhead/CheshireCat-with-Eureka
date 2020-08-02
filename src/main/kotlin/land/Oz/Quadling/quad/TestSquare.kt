package land.Oz.Quadling.quad

import game.Primitive
import game.main.Univ
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.withLock
import land.Oz.Quadling.cat.*
import math.matrix.InArrModel
import math.randomVec3
import uno.glfw.Key
import uno.glfw.MouseButton
import vulkan.buffer.makeDataVI_Dynamic
import vulkan.command.BindMVP
import vulkan.command.bindSet
import vulkan.drawing.DataVI
import vulkan.pipelines.descriptor.fetchModel

/**
 * Created by CowardlyLion on 2020/8/2 22:00
 */
class TestSquare(univ: Univ) : Primitive(univ){

    lateinit var sq: Squares
    lateinit var data: DataVI


    lateinit var model: InArrModel
    lateinit var bind: BindMVP

    val fpv = univ.matrices.fpv
    val mvp = univ.vulkan.descriptorSets.mvp

    override suspend fun initialize() {
        model = univ.fetchModel()
        bind = univ.bindSet(model)

        val spawn = fpv.forward(-10F)
        sq = Squares(
            center = spawn,
            right = Vec3(0F, 0F, -5F),
            face = Vec3(-1F, 0F, 0F)
        )
        sq.addSquare()

        val (vert, ind) = sq.data()
        data = univ.makeDataVI_Dynamic(vert, ind)
        data.descriptors.assign(bind)

        univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
        univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
            univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
        }

        univ.events.keyPress.subscribe { (key, mods) ->
            if (key == Key.B) {

                sq.mutex.withLock {
                    sq.addSquare()
                }
            }

        }


        univ.events.mouseScroll.subscribe { (delta) ->
            sq.mutex.withLock {
                sq.right += delta * forwardDelta
            }
        }

        univ.events.perSecond.subscribe { sec ->
            sq.mutex.withLock {
                Univ.logger.info {
                    "right ${sq.right}"
                }
            }
        }

        univ.events.mousePress.subscribe { (button, mods) ->
            if (button.i == MouseButton.RIGHT.i) {

            }
        }
        univ.events.mouseRelease.subscribe { (button, mods) ->
            if (button.i == MouseButton.RIGHT.i) {

            }
        }

    }

    val forwardDelta = 0.1F

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        sq.select(fpv.pos.p, fpv.direction())
        sq.update()
    }

    override suspend fun updateBuffer(imageIndex: Int) {
        val (vert, ind) = sq.data()
        data.vertex.arr = vert
        data.index.arr = ind
        data.vertex.reload_Dynamic()
        data.index.reload_Dynamic()
        model.update()
    }

    override suspend fun destroy() {
        data.destroy()
        model.destroy()
    }
}