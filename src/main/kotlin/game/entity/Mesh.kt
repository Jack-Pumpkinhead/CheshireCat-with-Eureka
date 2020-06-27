package game.entity

import assimp.AiMesh

/**
 * Created by CowardlyLion on 2020/6/1 21:15
 */
class Mesh(val vertex: List<Float>, val texCoord: List<Float>, val indices: List<Int>, val materialIndex: Int) {

    constructor(mesh: AiMesh) : this(
        mesh.vertices.flatMap { listOf(it.x, it.y, it.z) },
        mesh.textureCoords[0].flatMap { it.toList() },
        mesh.faces.flatten(),
        mesh.materialIndex
    )



}