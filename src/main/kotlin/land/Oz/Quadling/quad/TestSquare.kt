package land.Oz.Quadling.quad

import game.Primitive
import game.main.Univ
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xyz
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import land.Oz.Quadling.cat.blue2
import land.Oz.Quadling.cat.purple
import land.Oz.Quadling.cat.red
import math.matrix.InArrModel
import math.matrix.Model
import math.random
import math.randomVec3
import physics.NewtonPoint
import physics.hooke
import uno.glfw.Key
import uno.glfw.MouseButton
import vulkan.buffer.makeDataVI_Dynamic
import vulkan.command.BindMVP
import vulkan.command.bindSet
import vulkan.drawing.DataVI
import vulkan.pipelines.descriptor.fetchModel
import kotlin.math.abs

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

            if (key == Key.X) {

                univ.scope.launch {
                    datas = (0..3).map {
                        val square = sq.squares[it]
                        val vert = mutableListOf<Float>()
                        square.vertexData(vert, -square.center.p)
                        val ind = mutableListOf<Int>()
                        square.indexData(ind, 0)
                        univ.makeDataVI_Dynamic(vert.toFloatArray(), ind.toIntArray())
                    }
                    datas!!.forEach {
                        univ.vulkan.graphicPipelines.line.obj.arr.assign(it)
                    }
                    univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
                        datas!!.forEach {
                            univ.vulkan.graphicPipelines.line.obj.arr.assign(it)
                        }
                    }

                    delay(100L)
                    var i = 0
                    addJump(sq.squares[i++].center.p.xyz)

                    delay(2000L)
                    addJump(sq.squares[i++].center.p.xyz)
                    delay(3000L)
                    addJump(sq.squares[i++].center.p.xyz)
                    delay(4000L)
                    addJump(sq.squares[i++].center.p.xyz)

                    repeat(100){
                        delay(5000L)
                        addJump(sq.squares[i++%4].center.p.xyz)
                    }

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
                sq.mutex.withLock {
                    sq.drawMode = true


                }
            }
        }
        univ.events.mouseRelease.subscribe { (button, mods) ->
            if (button.i == MouseButton.RIGHT.i) {
                sq.mutex.withLock {
                    sq.drawMode = false
//                    sq.firstGen = true
                    for (i in sq.lastGens.indices) {
                        sq.lastGens[i] = -1
                    }

                }
            }
        }


        rotCursors += NewtonPoint(
            p = fpv.forward(1F) + randomVec3(0.1F),
            v = Vec3(0.01F, 0F, 0F)
        )
        rotCursors += NewtonPoint(
            p = fpv.forward(1F) + randomVec3(0.01F),
            v = randomVec3(0.01F)
        )
        rotCursors += NewtonPoint(
            p = fpv.forward(1F) + randomVec3(0.01F),
            v = randomVec3(0.01F)
        )
        sq.lastGens += -1
        sq.lastGens += -1
        sq.lastGens += -1




    }

    val forwardDelta = 0.1F


    val rotCursors = mutableListOf<NewtonPoint>()
    val colors = mutableListOf<Vec3>(purple, blue2, red)

    var datas: List<DataVI>? = null
    val jumps = mutableListOf<NewtonPoint>()
    val jumpsSampleTime = mutableListOf<Long>()
    var jumpDelay = 1
    val jumpModels = mutableListOf<InArrModel>()
    val jumpMutex = Mutex()

    suspend fun addJump(center: Vec3) {
        jumpMutex.withLock {
            val point = NewtonPoint(
                p = center, v = Vec3(
                    random(0.01F, 0.1F),
                    random(0.1F, 0.5F),
                    random(0.01F, 0.5F)
                )
            )
            point.v.y = -abs(point.v.y)
            jumps += point
            jumpsSampleTime += 0L
        }
    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        val forward = fpv.forward(1F)

        rotCursors.forEach { rotCursor ->
            rotCursor.f.put(0, 0, 0)
            rotCursor.f.plusAssign(hooke(rotCursor.p, forward, 2.0, 100F))
            rotCursor.update()
        }
        jumpMutex.withLock {

            jumps.forEach { point ->
                point.f.put(0, 0.4F, 0)
                if (point.p.y > 5F && point.v.y > 0) {
                    point.v.y = -abs(point.p.y) * random(0.3F, 0.7F)
                }
                point.update()
            }
            if (tick % jumpDelay == 0L) {
                jumps.forEachIndexed { i, point ->
                    val model = univ.fetchModel()
                    model.pos = point.p
                    model.update()
                    jumpModels += model
                    val bind = univ.bindSet(model)
                    if (datas != null && jumpsSampleTime[i] < 1000) {
                        datas!![i % 4].descriptors.assign(bind)
                        jumpsSampleTime[i]++
                    }

                }
            }
        }

        if (!sq.drawMode) {
            sq.select(fpv.pos.p, fpv.direction())
        }
        sq.mutex.withLock {
            if (sq.drawMode) {
                if (sq.selected.isNotEmpty()) {
                    for (i in rotCursors.indices) {

                        sq.tryGenerate(
                            pos = NewtonPoint(
//                            p = fpv.forward(1F),
                                p = forward + (rotCursors[i].p - forward) * 0.1F,
                                v = fpv.direction().times(0.2F)
                            ),
                            point = sq.selected.last(),
                            index = i,
                            color = colors[i]
                        )
                    }
                }


            }
        }
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