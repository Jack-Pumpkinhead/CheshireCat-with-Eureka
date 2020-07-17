package land.Oz.Munchkin

import game.Primitive
import game.entity.VisualGraphWrap
import game.main.Univ
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.vector.distance2
import physics.NewtonPoint
import physics.VisualGraph
import uno.glfw.Key
import uno.glfw.MouseButton
import vulkan.drawing.OzObjectTextured2
import vulkan.drawing.putMultiObject
import vulkan.pipelines.PipelineTextured

/**
 * Created by CowardlyLion on 2020/7/13 23:47
 */
class TestIcosphere(univ: Univ) : Primitive(univ) {

    init {
//        instantiate = false
    }
    //    lateinit var obj0: OzObjectTextured
//    lateinit var obj1: OzObjectTextured
//    lateinit var obj2: OzObjectTextured
//    lateinit var objs: List<OzObjectTextured>

    //    lateinit var list: List<OzObjectTextured>

    lateinit var multiObject: PipelineTextured.MultiObject

    val mutex = Mutex()
    val graphs = mutableListOf<VisualGraphWrap>()
    var activeGraph: VisualGraphWrap? = null

    val fpv = univ.matrices.fpv
    val images = univ.vulkan.images
    val models = univ.vulkan.layoutMVP.model

    override suspend fun initialize() {
       /* obj0 = univ.putObject(
            univ.emeralds.icosphere.find("Icosphere")!!.meshes[0],
            univ.vulkan.images.Icosphere_green
        )
        obj1 = univ.putObject(
            univ.emeralds.icosphere.find("Icosphere")!!.meshes[0],
            univ.vulkan.images.Icosphere_blue
        )
        obj2 = univ.putObject(
            univ.emeralds.icosphere.find("Icosphere")!!.meshes[0],
            univ.vulkan.images.Icosphere_red
        )*/
        /*list = (1..1000).map {
            univ.putObject(
                univ.emeralds.icosphere.find("Icosphere")!!.meshes[0],
                univ.vulkan.images.Icosphere_red,
                true
            )
        }*/

//        objs = listOf(obj0, obj1, obj2)

        multiObject = univ.putMultiObject(univ.emeralds.icosphere.find("Icosphere")!!.meshes[0])


        univ.events.keyPress.subscribe { (key, mods) ->
            if (key == Key.X) {
                mutex.withLock {
                    snapActive()
                }
            }
            if (key == Key.G) {
                addGraph()
            }
            if (key == Key.V) {
                addPoint()
            }

        }


        multiObject.mutex.withLock {
            val objs = multiObject.objs
            objs.clear()
            for (graph in graphs) {
                objs += OzObjectTextured2(
                    texIndex = if (graph == activeGraph) images.Icosphere_red else images.Icosphere_blue,
                    model = graph.pivotModel,
                    visible = true
                )

                graph.pointsModel.forEach { pointModel ->
                    objs += OzObjectTextured2(
                        texIndex = images.Icosphere_green,
                        model = pointModel,
                        visible = true
                    )
                }

            }
        }

    }


    fun snapActive() {
        activeGraph = if (graphs.isNotEmpty()) {
            graphs.minBy { distance2(it.graph.pivot.p, fpv.forward(1F)) }
        } else null

    }

    suspend fun addGraph() {
        mutex.withLock {
            val graph = VisualGraphWrap(
                univ = univ,
                graph = VisualGraph(
                    pivot = NewtonPoint(p = fpv.forward(1F)),
                    numPoints = 1
                )
            )
            graph.init()
            graphs += graph

            multiObject.mutex.withLock {

                multiObject.objs += OzObjectTextured2(
//                            texIndex = if (graph == activeGraph) images.Icosphere_red else images.Icosphere_blue,
                    texIndex = images.Icosphere_blue,
                    model = graph.pivotModel,
                    visible = true
                )
            }

        }
    }

    suspend fun addPoint() {
        mutex.withLock {
            val model = activeGraph?.addObj(point = NewtonPoint(p = fpv.forward(1F)))
            if (activeGraph != null) {  //potential bug

                multiObject.mutex.withLock {
                    multiObject.objs += OzObjectTextured2(
                        texIndex = images.Icosphere_green,
                        model = model!!,
                        visible = true
                    )
                }
            }


        }
    }


    override suspend fun gameloop(tick: Long, timemillis: Long) {
//       objs.forEach {
//           it.update()
//       }
//        list.forEach { it.update() }
        mutex.withLock {

            graphs.forEach { it.update() }


        }

    }

    override suspend fun destroy() {
        multiObject.data.destroy()
    }
}