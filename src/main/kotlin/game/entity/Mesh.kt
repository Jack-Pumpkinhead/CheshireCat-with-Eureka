package game.entity

import assimp.AiMesh
import game.join

/**
 * Created by CowardlyLion on 2020/6/1 21:15
 */
//class Mesh(val vertex: List<Float>, val texCoord: List<Float>, val indices: List<Int>, val materialIndex: Int) {
class Mesh(val mesh: AiMesh){


    val vertex: List<Float> = mesh.vertices.flatMap { listOf(it.x, it.y, it.z) }
    fun texCoord(index: Int) = mesh.textureCoords[index].flatMap { it.toList() }
    val indices: List<Int> = mesh.faces.flatten()
    val materialIndex: Int = mesh.materialIndex

    init {

//        mesh.colors

    }

    fun vertexArray() = join(vertex, texCoord(0))

}