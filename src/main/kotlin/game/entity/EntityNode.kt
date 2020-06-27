package game.entity

import assimp.AiMesh
import assimp.AiNode
import glm_.mat4x4.Mat4

/**
 * Created by CowardlyLion on 2020/6/1 21:10
 */
class EntityNode(val name:String,val transform: Mat4 = Mat4(), val meshes: List<Mesh>) {

    constructor(aiNode: AiNode, meshes: List<AiMesh>) : this(
        aiNode.name,
        aiNode.transformation,
        aiNode.meshes.map { index -> Mesh(meshes[index]) }
    )


}