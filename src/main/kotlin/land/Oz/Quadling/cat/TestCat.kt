package land.Oz.Quadling.cat

import game.Primitive
import game.main.Univ
import kotlinx.coroutines.sync.withLock
import land.Oz.Quadling.knot.Knot
import math.matrix.InArrModel
import math.randomVec3
import physics.NewtonPoint
import uno.glfw.Key
import vulkan.buffer.makeDataVI_Dynamic
import vulkan.command.BindMVP
import vulkan.command.bindSet
import vulkan.drawing.DataVI
import vulkan.pipelines.descriptor.fetchModel

/**
 * Created by CowardlyLion on 2020/7/31 20:19
 */
class TestCat(univ: Univ) : Primitive(univ) {

    lateinit var cat: CatContext
    lateinit var data: DataVI


    lateinit var model: InArrModel
    lateinit var bind: BindMVP

    val fpv = univ.matrices.fpv
    val mvp = univ.vulkan.descriptorSets.mvp


    var initialized = false
    override suspend fun initialize() {
        model = univ.fetchModel()
        bind = univ.bindSet(model)

        univ.events.keyPress.subscribe { (key, mods) ->
            if (key == Key.K) {
                val spawn = fpv.forward(1F)
                cat = CatContext(
                    points = mutableListOf(
                        tetrahedron(spawn),
                        tetrahedron(randomVec3(spawn,1F)),
                        tetrahedron(randomVec3(spawn,1F)),
                        tetrahedron(randomVec3(spawn,1F)),
                        tetrahedron(randomVec3(spawn,1F))
                    )
                )

                val (vert, ind) = cat.data()
                data = univ.makeDataVI_Dynamic(vert, ind)
                data.descriptors.assign(bind)
                initialized = true

                univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
                univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
                    univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
                }

                univ.events.perSecond.subscribe { sec ->
                    cat.mutex.withLock {
                        Univ.logger.info {
                            "size ${cat.points.size}}"
                        }
                        Univ.logger.info {
                            "selected ${cat.selected.joinToString { it.center.p.toString() }}}"
                        }



                    }
                }

            }

            if (key == Key.H) {

                if (cat.selected.size >= 2) {
                    val s = cat.selected[cat.selected.size - 2]
                    val t = cat.selected[cat.selected.size - 1]
                    cat.addLine(s,t, CatHom.Line.basic)
                }

            }
        }


    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        if (initialized) {
            cat.select(fpv.pos.p, fpv.direction())
            cat.update()
        }
    }

    override suspend fun updateBuffer(imageIndex: Int) {
        if (initialized) {

            val (vert, ind) = cat.data()
            data.vertex.arr = vert
            data.index.arr = ind
            data.vertex.reload_Dynamic()
            data.index.reload_Dynamic()
            model.update()
        }
    }

    override suspend fun destroy() {
        data.destroy()
        model.destroy()
    }
}