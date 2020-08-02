package land.Oz.Quadling.cat

import game.Primitive
import game.main.Univ
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.withLock
import math.matrix.InArrModel
import math.randomVec3
import uno.glfw.Key
import uno.glfw.MouseButton
import vulkan.buffer.makeDataVI_Dynamic
import vulkan.command.BindMVP
import vulkan.command.bindSet
import vulkan.drawing.DataVI
import vulkan.pipelines.descriptor.fetchModel
import kotlin.random.Random

/**
 * Created by CowardlyLion on 2020/7/31 20:19
 */
class TestCat(univ: Univ) : Primitive(univ) {

    init {
        instantiate = false
    }

    lateinit var cat: CatGraph
    lateinit var data: DataVI


    lateinit var model: InArrModel
    lateinit var bind: BindMVP

    val fpv = univ.matrices.fpv
    val mvp = univ.vulkan.descriptorSets.mvp


    var initialized = false
    override suspend fun initialize() {
        model = univ.fetchModel()
        bind = univ.bindSet(model)

//        univ.events.keyPress.subscribe { (key, mods) ->
//            if (key == Key.K) {
                val spawn = fpv.forward(1F)
                cat = CatGraph(
                    center = cubeExact(spawn,5F),
                    points = mutableListOf(
                        tetrahedron(spawn),
                        tetrahedron(randomVec3(spawn, 1F)),
                        cube(randomVec3(spawn, 1F)),
                        cube(randomVec3(spawn, 1F)),
                        cube(randomVec3(spawn, 1F)),
                        cubeExact(randomVec3(spawn, 1F)),
                        cubeExact(randomVec3(spawn, 1F))
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

//            }
        univ.events.keyPress.subscribe { (key, mods) ->
            if (key == Key.H) {

                cat.mutex.withLock {
                    if (cat.selected.size >= 2) {
                        val s = cat.selected[cat.selected.size - 2]
                        val t = cat.selected[cat.selected.size - 1]
                        cat.addLine(s, t, CatHom.Line.basic)
                    }
                }

            }
            if (key == Key.B) {

                cat.mutex.withLock {
                    cat.points += cube(fpv.forward(1F))
                }
            }
            if (key == Key.C) {

                cat.mutex.withLock {
                    if (cat.selected.isNotEmpty()) {

                        val select = cat.selected[cat.selected.size - 1]
                        if (select.points.size > 1) {
                            val a = Random.nextInt(select.points.size - 1)
                            val b = a + Random.nextInt(select.points.size - a - 1) + 1
                            val temp = select.points[a]
                            select.points[a] = select.points[b]
                            select.points[b] = temp
                        }
                    }

                }
            }

        }

        univ.events.mouseScroll.subscribe { (delta) ->
            forward += delta * forwardDelta
        }


            univ.events.mousePress.subscribe { (button, mods) ->
                Univ.logger.info {
                    "press i: ${button.i}"
                }
                if (button.i == MouseButton.LEFT.i) {
                    cat.mutex.withLock {
                        if (cat.selected.isNotEmpty()) {
                            dragging = true
                            cat.draged = cat.selected[cat.selected.size - 1]
                            cat.dragPoint = fpv.forward(forward)
                            cat.dragView = smallTetrahedron(Vec3())
                        }
                    }
                }

            }
            univ.events.mouseRelease.subscribe { (button, mods) ->
                Univ.logger.info {
                    "release i: ${button.i}"
                }
                if (button.i == MouseButton.LEFT.i) {
                    if (dragging) {
                        cat.mutex.withLock {
                            dragging = false
                            cat.draged = null
                            cat.dragPoint = null
                            cat.dragView = null //maybe delay
                        }
                    }
                }

            }
        }

    var dragging = false
    var forward = 1F
    val forwardDelta = 0.1F

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        if (initialized) {
            cat.select(fpv.pos.p, fpv.direction())
            cat.mutex.withLock {
                if (dragging) {
                    cat.dragPoint = fpv.forward(forward)
                }
            }
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
        if (initialized) {
            data.destroy()
        }
        model.destroy()
    }
}