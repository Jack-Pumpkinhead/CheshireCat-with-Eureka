package land.Oz.Quadling.texting

import game.Primitive
import game.event.KeyPress
import game.main.Univ
import glm_.vec3.swizzle.xyz
import math.matrix.InArrModel
import math.matrix.Model
import physics.NewtonPoint
import uno.glfw.Key
import vulkan.buffer.makeDataVI
import vulkan.drawing.DataVI
import vulkan.pipelines.descriptor.fetchModel

/**
 * Created by CowardlyLion on 2020/7/25 22:31
 */
class TestText(univ: Univ) : Primitive(univ) {


    lateinit var alignment: TextAlignment
//    lateinit var model: InArrModel
    lateinit var data: DataVI


    var put = false

    val fpv = univ.matrices.fpv
    var ini = false
    var textingI = 1
    override suspend fun initialize() {
        val mesh = univ.emeralds.icosphere.find("Icosphere")!!.meshes[0]
        data = univ.makeDataVI(mesh.vertexTex(), mesh.indicesArr)
        univ.vulkan.graphicPipelines.singleTexture.obj.arr.assign(data)
        univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
            univ.vulkan.graphicPipelines.singleTexture.obj.arr.assign(data)
        }
        univ.events.keyPress.subscribe { (key) ->
            if (key == Key.P) {
                put = true
                alignment = TextAlignment(univ, NewtonPoint(fpv.pos.p.xyz))
//                model = univ.fetchModel()
//                model.pos = fpv.pos.p
//                model.rot = fpv.mouseRotation.rot

                alignment.init()
               alignment.textPoolModelB.forEach {
                   data.descriptors.assign(it)
               }
                Univ.logger.info {
                    "???"
                }
                ini = true

            }
            if (key == Key.RIGHT) {
                alignment.texting()
                Univ.logger.info {
                    "texting ${textingI++}"
                }
            }
        }



    }

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        if (ini) {

//            model.update()
            alignment.update()
        }
    }

    override suspend fun updateBuffer(imageIndex: Int) {

    }

    override suspend fun destroy() {
//        model.destroy()
    }
}