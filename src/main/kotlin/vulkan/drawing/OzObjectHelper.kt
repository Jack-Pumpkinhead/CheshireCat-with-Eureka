package vulkan.drawing

import game.entity.Mesh
import game.getTexturedObj
import game.getVertexOnlyMultiObj
import game.main.Univ
import kotlinx.coroutines.sync.withLock
import math.matrix.Model
import math.randomVec3
import vkk.identifiers.CommandBuffer
import vulkan.pipelines.PipelineTextured
import vulkan.pipelines.PipelineVertexOnly

/**
 * Created by CowardlyLion on 2020/7/8 23:09
 */
class OzObjectTextured(val data: PipelineTextured.ObjStatic, val model: Model, var visible: Boolean = true) {

    suspend fun update() {
        model.update()
    }

    fun record(cb: CommandBuffer, imageIndex: Int) {
        if (visible) {
            data.recorder.invoke(cb, imageIndex)
        }
    }

}
class OzObjectTextured2(var texIndex: Int, var model: Model, var visible: Boolean) {


    suspend fun update() {
        model.update()
    }

}
class OzObjectSimple(var model: Model, var visible: Boolean) {

    suspend fun update() {
        model.update()
    }

}


suspend fun Univ.getTexturedObject(mesh: Mesh, texIndex: Int): OzObjectTextured {
    val model = vulkan.layoutMVP.model.fetch()
    return OzObjectTextured(getTexturedObj(mesh, model.index, texIndex), model)
}
suspend fun Univ.putTexturedObject(mesh: Mesh, texIndex: Int, visible: Boolean): OzObjectTextured {
    val model = vulkan.layoutMVP.model.fetch()
    model.pos.plusAssign(randomVec3(20F))

    val data = getTexturedObj(mesh, model.index, texIndex)
//    frameLoop.drawCmds3.withLock { cmds ->
//        cmds.add(data.recorder)
//    }
    val obj = OzObjectTextured(data, model, visible)
    objects.mutex.withLock {
        objects.textured += obj
    }
    return obj
}

suspend fun Univ.getMultiObject(mesh: Mesh): PipelineTextured.MultiObject {
    val mObj = PipelineTextured.MultiObject(
        pipeline = vulkan.graphicPipelines.hellotexture.graphicsPipeline,
        pipelineLayout = vulkan.graphicPipelines.hellotexture.layout,
        data = vulkan.buffer.staticObject_deviceLocal(mesh.vertexTex(), mesh.indices.toIntArray()),
        layoutMVP = vulkan.layoutMVP,
        textureSets = vulkan.textureSets
    )
    events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
        mObj.pipeline = vulkan.graphicPipelines.hellotexture.graphicsPipeline
    }
    return mObj
}
suspend fun Univ.putMultiObject(mesh: Mesh): PipelineTextured.MultiObject {
    val mObj = getMultiObject(mesh)
    frameLoop.multiObject.assign(mObj)
    return mObj
}
suspend fun Univ.putVertexOnlyMultiObject(mesh: Mesh): PipelineVertexOnly.MultiObject {
    val mObj = getVertexOnlyMultiObj(mesh)
    frameLoop.multiObject_vertexOnly.assign(mObj)
    return mObj
}
