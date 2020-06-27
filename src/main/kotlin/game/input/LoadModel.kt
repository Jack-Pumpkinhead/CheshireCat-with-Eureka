package game.input

import assimp.AiNode
import assimp.AiPostProcessStep
import assimp.Importer
import assimp.or
import game.entity.Emerald
import game.entity.EntityNode
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vulkan.OzVulkan
import java.util.*

/**
 * Created by CowardlyLion on 2020/6/1 19:46
 */
object LoadModel {
    val importer = Importer()


    private val cache = mutableMapOf<String, Emerald>()

    val mutex = Mutex()

    suspend fun get(path: String) {
        mutex.withLock {
            cache.computeIfAbsent(path){
                load(path)
            }
        }
    }
    private fun load(path: String): Emerald {
        val aiScene = importer.readFile(
            LoadFile.url(path),
            AiPostProcessStep.JoinIdenticalVertices.or(
                AiPostProcessStep.Triangulate).or(
                AiPostProcessStep.FixInfacingNormals
            )
        )
        if (aiScene == null) {
            OzVulkan.logger.info { importer.errorString }
            return Emerald.NULL
        }



        nodeStack += null to aiScene.rootNode

        val emerald = Emerald()

        while (nodeStack.isNotEmpty()) {
            val (parentNode, node) = nodeStack.pop()
            val entityNode = EntityNode(node, aiScene.meshes)
            emerald.structure.addNode(entityNode)
            if (parentNode != null) {
                emerald.structure.putEdge(parentNode, entityNode)
            }

            node.children.forEach { child ->
                nodeStack += entityNode to child
            }
        }

        importer.freeScene()

        return emerald
    }


    val nodeStack = Stack<Pair<EntityNode?, AiNode>>()  //parent to children

}