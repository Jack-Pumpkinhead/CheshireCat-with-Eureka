package game.entity

import game.main.Univ
import math.matrix.Model
import math.randomVec3
import physics.NewtonPoint
import physics.VisualGraph

/**
 * Created by CowardlyLion on 2020/7/15 19:56
 */
class VisualGraphWrap(val univ: Univ, val graph: VisualGraph) {

    val fpv = univ.matrices.fpv
    val images = univ.vulkan.images
    val models = univ.vulkan.layoutMVP.model

    lateinit var pivotModel: Model
    val pointsModel = mutableListOf<Model>()

    val mutex = graph.mutex

    suspend fun init() {
        pivotModel = models.fetch()
        pivotModel.pos = graph.pivot.p
        pivotModel.scale = 0.5F

        graph.points.forEach {
            val m = models.fetch()
            m.pos = it.p
            m.scale = 0.5F

            pointsModel += m

        }

    }

    suspend fun update() {
        graph.update()
        pivotModel.update()
        pointsModel.forEach {
            it.update()
        }
    }

    suspend fun addObj(point: NewtonPoint = NewtonPoint(p = randomVec3(graph.pivot.p, 10F))): Model {
        graph.addObj(point)
        val m = models.fetch()
        m.pos = point.p
        m.scale = 0.5F

        pointsModel += m
        return m
    }
    suspend fun removeObj(i: Int) {
        graph.removeObj(i)
        pointsModel.removeAt(i).destroy()
    }

    suspend fun destroy() {
        pivotModel.destroy()
        pointsModel.forEach {
            it.destroy()
        }
    }


}