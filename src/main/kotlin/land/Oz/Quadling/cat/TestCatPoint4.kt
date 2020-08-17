package land.Oz.Quadling.cat

import game.Primitive
import game.entity.cursor.RotatingCursor
import game.main.Univ
import glm_.vec3.Vec3
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import land.Oz.Quadling.quad.Squares
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
 * Created by CowardlyLion on 2020/8/13 12:28
 */
class TestCatPoint4(univ: Univ) : Primitive(univ){

    val cats = mutableListOf<CatGraph4>()
    val datas = mutableListOf<DataVI>()

    val models = mutableListOf<InArrModel>()
    val binds = mutableListOf<BindMVP>()

    val fpv = univ.matrices.fpv
    val mvp = univ.vulkan.descriptorSets.mvp


    val mutex = Mutex()

    override suspend fun initialize() {

        univ.events.keyPress.subscribe { (key, mods) ->
            if (key == Key.G) {
                mutex.lock()

                val model = univ.fetchModel()
                val bind = univ.bindSet(model)

                val spawn = fpv.forward(1F)
                val cat = CatGraph4(
                    boundary = cubeExact4(spawn, 1F),
                    objs = mutableListOf()
                )

                val (vert, ind) = cat.data()
                val data = univ.makeDataVI_Dynamic(vert, ind)
                data.descriptors.assign(bind)

                univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
                univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
                    univ.vulkan.graphicPipelines.line.obj.arr.assign(data)
                }
                cats += cat
                datas += data
                models += model
                binds += bind

                mutex.unlock()
            }
            if (key == Key.V) {
                if (cats.isNotEmpty()) {
                    mutex.lock()

                    val spawn = fpv.forward(1F)
                    cats.last().addObj(cubeExact4(spawn, 0.01F, 0.02F))

                    mutex.unlock()
                }

            }
        }

        val spawn = fpv.forward(1F)
        rotC.add(spawn)
        rotC.add(spawn)
        rotC.add(spawn)
        val (vert, ind) = rotC.data()
        rotData = univ.makeDataVI_Dynamic(vert, ind)
        rotModel = univ.fetchModel()
        rotBind = univ.bindSet(rotModel)
        rotData.descriptors.assign(rotBind)

        univ.vulkan.graphicPipelines.line.obj.arr.assign(rotData)
        univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
            univ.vulkan.graphicPipelines.line.obj.arr.assign(rotData)
        }
        initialized = true
    }
    var initialized = false


    val rotC = RotatingCursor(center = NewtonPoint())
    lateinit var rotData: DataVI
    lateinit var rotModel : InArrModel
    lateinit var rotBind : BindMVP

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        mutex.lock()
        val forward = fpv.forward(1F)

        cats.forEach { it.update() }

        cats.forEach {
            it.select(fpv.pos.p, fpv.direction())
        }

        rotC.mutex.withLock {
            rotC.center.p.put(forward)
            rotC.update()
        }

        mutex.unlock()
    }

    override suspend fun updateBuffer(imageIndex: Int) {
        mutex.lock()
        for (i in cats.indices) {
            val (vert, ind) = cats[i].data()
            val data = datas[i]
            data.vertex.arr = vert
            data.index.arr = ind
            data.vertex.reload_Dynamic()
            data.index.reload_Dynamic()
            models[i].update()
        }

        rotC.mutex.withLock {
            val (vert, ind) = rotC.data()
            rotData.vertex.arr = vert
            rotData.index.arr = ind
            rotData.vertex.reload_Dynamic()
            rotData.index.reload_Dynamic()
            rotModel.update()
        }

        mutex.unlock()
    }

    override suspend fun destroy() {
        mutex.withLock {

            for (i in cats.indices) {
                datas[i].destroy()
                models[i].destroy()
            }
            rotModel.destroy()
        }
    }

}