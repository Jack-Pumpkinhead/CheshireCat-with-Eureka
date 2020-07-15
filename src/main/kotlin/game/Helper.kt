package game

import game.entity.Mesh
import game.main.Univ
import vulkan.pipelines.PipelineTextured

/**
 * Created by CowardlyLion on 2020/7/13 23:32
 */

fun Univ.getTexturedObj(mesh: Mesh, matrixIndex: Int, texIntex: Int): PipelineTextured.ObjStatic {
    return PipelineTextured.ObjStatic(
        univ = this,
        pos_texCoord = join(mesh.vertex, mesh.texCoord(0)),
        indices = mesh.indices.toIntArray(),
        matrixIndex = matrixIndex,
        texIndex = texIntex
    )
}

fun join(pos: List<Float>, tex: List<Float>): FloatArray {
    val arr = mutableListOf<Float>()
    var i = 0
    var j = 0
    while (i < pos.size) {
        /*arr += pos[i]
        arr += pos[i + 1]
        arr += pos[i + 2]
        arr += tex[j]
        arr += tex[j + 1]
        i += 3
        j += 2*/
        arr += pos[i++]
        arr += pos[i++]
        arr += pos[i++]
        arr += tex[j++]
        arr += tex[j++]
    }
    return arr.toFloatArray()
}