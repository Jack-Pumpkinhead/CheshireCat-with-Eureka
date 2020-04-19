package land.oz.turorial

import graphics.scenery.spirvcrossj.*
import kool.ByteBuffer
import mu.KotlinLogging
import java.nio.ByteBuffer

/**
 * Created by CowardlyLion on 2020/4/16 17:13
 */
class GLSLToVulkan {

    val logger = KotlinLogging.logger {}

    var vertex: ByteBuffer = ByteBuffer(0)
    var fragment: ByteBuffer = ByteBuffer(0)


    init {
        Loader.loadNatives()
        val file = this.javaClass.classLoader.getResourceAsStream("shaders\\glsl\\hellotriangle.vert")!!
            .readAllBytes().toString(Charsets.UTF_8)

        assert(libspirvcrossj.initializeProcess())

        val resources = libspirvcrossj.getDefaultTBuiltInResource()
        val program = TProgram()

        val shaderType = EShLanguage.EShLangVertex
        val shader = TShader(shaderType)
        shader.setStrings(arrayOf(file), 1)
        shader.setAutoMapBindings(true)
        val messages = EShMessages.EShMsgDefault or EShMessages.EShMsgVulkanRules or EShMessages.EShMsgSpvRules

        assert(shader.parse(resources, 460, false, messages)) { "compile fail" }
        program.addShader(shader)
        assert(program.link(EShMessages.EShMsgDefault)) { "link fail" }
        assert(program.mapIO()) { "mapIO fail" }
        val spirv_vec = IntVec()
        libspirvcrossj.glslangToSpv(program.getIntermediate(shaderType), spirv_vec)
        logger.info { "cap: ${spirv_vec.capacity()} byte: " }
        logger.info { spirv_vec.joinToString(separator = "_",transform = {l -> l.toString() }) }
        logger.info { spirv_vec.joinToString(separator = "_",transform = {l -> l.toByte().toString() }) }

//        vertex = ByteBuffer(spirv_vec.size*Long.SIZE_BYTES)
//        spirv_vec.forEach { vertex.putLong(it) } //

//        vertex = ByteBuffer(spirv_vec.size)
//        spirv_vec.forEach { vertex.put(it.toByte()) } //

        vertex = ByteBuffer(spirv_vec.size*Int.SIZE_BYTES)
        spirv_vec.forEach { vertex.putInt(it.toInt()) } //

        vertex.flip()   //Important!

        loadFrag()

        libspirvcrossj.finalizeProcess()



    }

    private fun loadFrag() {
        val file = this.javaClass.classLoader.getResourceAsStream("shaders\\glsl\\hellotriangle.frag")!!
            .readAllBytes().toString(Charsets.UTF_8)

        assert(libspirvcrossj.initializeProcess())

        val resources = libspirvcrossj.getDefaultTBuiltInResource()
        val program = TProgram()

        val shaderType = EShLanguage.EShLangFragment
        val shader = TShader(shaderType)
        shader.setStrings(arrayOf(file), 1)
        shader.setAutoMapBindings(true)
        val messages = EShMessages.EShMsgDefault or EShMessages.EShMsgVulkanRules or EShMessages.EShMsgSpvRules

        assert(shader.parse(resources, 460, false, messages)) { "compile fail" }
        program.addShader(shader)
        assert(program.link(EShMessages.EShMsgDefault)) { "link fail" }
        assert(program.mapIO()) { "mapIO fail" }
        val spirv_vec = IntVec()
        libspirvcrossj.glslangToSpv(program.getIntermediate(shaderType), spirv_vec)
        logger.info { "cap: ${spirv_vec.capacity()} byte: " }
        logger.info { spirv_vec.joinToString(separator = "_",transform = {l -> l.toString() }) }
        logger.info { spirv_vec.joinToString(separator = "_",transform = {l -> l.toByte().toString() }) }

//        fragment = ByteBuffer(spirv_vec.size*Long.SIZE_BYTES)
//        spirv_vec.forEach { fragment.putLong(it) }

//        fragment = ByteBuffer(spirv_vec.size)
//        spirv_vec.forEach { fragment.put(it.toByte()) }

        fragment = ByteBuffer(spirv_vec.size*Int.SIZE_BYTES)
        spirv_vec.forEach { fragment.putInt(it.toInt()) }   //TRUE IntArray!

        fragment.flip() //Important!!

    }
}