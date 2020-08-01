package game.buildin

import game.main.Univ
import vulkan.buffer.makeDataVI
import vulkan.drawing.DataVI

/**
 * Created by CowardlyLion on 2020/7/26 10:49
 */
class BuildInDataVI(val univ: Univ, val buildIn: Buildin) {


    lateinit var data: DataVI


    suspend fun init() {
        data = univ.makeDataVI(buildIn.mesh.Icosphere.vertexTex(), buildIn.mesh.Icosphere.indicesArr)

        univ.vulkan.graphicPipelines.singleTexture.obj.arr.assign(data)

        univ.events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
            univ.vulkan.graphicPipelines.singleTexture.obj.arr.assign(data)
        }
    }


}