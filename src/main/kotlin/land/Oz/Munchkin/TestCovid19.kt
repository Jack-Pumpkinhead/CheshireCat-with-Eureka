package land.Oz.Munchkin

import game.Primitive
import game.main.Univ
import kotlinx.coroutines.sync.withLock
import math.matrix.Model
import math.randomVec3
import vulkan.drawing.*
import vulkan.pipelines.PipelineVertexOnly

/**
 * Created by CowardlyLion on 2020/7/16 18:53
 */
class TestCovid19(univ: Univ) : Primitive(univ) {


    init {
        instantiate = false
    }

    lateinit var cov: PipelineVertexOnly.MultiObject
    lateinit var model:Model

    override suspend fun initialize() {

        model = univ.vulkan.layoutMVP.model.fetch()
        model.pos = randomVec3(10F)

        cov = univ.putVertexOnlyMultiObject(
            univ.emeralds.get("Covid-19.dae")!!.find("Covid-19")!!.meshes[0]
        )
        cov.mutex.withLock {
            cov.objs += OzObjectSimple(
                model = model,
                visible = true
            )
        }
    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        model.update()
    }

    override suspend fun destroy() {
        cov.data.destroy()
    }
}