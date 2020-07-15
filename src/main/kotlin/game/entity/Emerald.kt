package game.entity

import assimp.AiScene
import com.google.common.graph.GraphBuilder
import kool.Stack

/**
 * Created by CowardlyLion on 2020/6/1 22:11
 */
class Emerald(val aiScene: AiScene) {
    val structure = GraphBuilder.directed().allowsSelfLoops(false).build<EntityNode>()
    fun find(name: String)= structure.nodes().find { it.name == name }

    init {
        val list = aiScene.textures.map { it.value }
        val textures = aiScene.textures
    }




}