package game.input

import assimp.AiNode
import assimp.AiPostProcessStep
import assimp.Importer
import assimp.or
import de.intarsys.cwt.freetype.Freetype
import de.intarsys.cwt.freetype.nativec.FTLibrary
import game.entity.Emerald
import game.entity.EntityNode
import gli_.Texture
import gli_.gli
import org.springframework.core.io.ResourceLoader
import vulkan.OzVulkan
import java.io.InputStream
import java.util.*
import javax.imageio.ImageIO

/**
 * Created by CowardlyLion on 2020/6/24 22:31
 */
class SpringInput(val resourceLoader: ResourceLoader) {


    fun get(path: String): InputStream = resourceLoader.getResource(path).inputStream
    fun url(path: String) = resourceLoader.getResource(path).url
    fun string(path: String) = get(path).readAllBytes().toString(Charsets.UTF_8)


    fun loadImage(name: String, flipY: Boolean = false): Texture {
        val image = gli.loadImage(ImageIO.read(get(name)), flipY)
//        gli.makeTexture2d()
//        val extent = image.extent(0)
//        val levels = image.levels()
        return image
    }

    fun loadModel(path: String): Emerald? {
        val importer = Importer()
        val aiScene = importer.readFile(
            url(path),
            AiPostProcessStep.JoinIdenticalVertices.or(
                AiPostProcessStep.Triangulate).or(
                AiPostProcessStep.FixInfacingNormals).or(
                AiPostProcessStep.FlipUVs
            )
        )
        if (aiScene == null) {
            OzVulkan.logger.info { importer.errorString }
            return null
        } else {


//        val texture = aiScene.textures["UVA"]!!



        val nodeStack = Stack<Pair<EntityNode?, AiNode>>()  //parent to children
        nodeStack += null to aiScene.rootNode

        val emerald = Emerald(aiScene)

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

//        importer.freeScene()

        return emerald
        }
    }

    init {
//        loadFont("font\\GrenzeGotisch-VariableFont_wght.ttf")

    }

    fun loadFont(path: String) {

//        GrenzeGotisch-VariableFont_wght.ttf
//FTLibrary.
        val library = Freetype.initFreeType()
        val face = library.newFace(url(path).file, 0)
        face.setCharSize(16 * 64, 16 * 64, 300, 300)
        face.getCharIndex('A'.toInt())
        face.loadGlyph(0,0)
        val glyphSlot = face.glyphSlot
        glyphSlot.renderGlyph(Freetype.RENDER_MODE_NORMAL)
        val bitmap = glyphSlot.glyphSlot.bitmap
        bitmap.bytes

    }



}