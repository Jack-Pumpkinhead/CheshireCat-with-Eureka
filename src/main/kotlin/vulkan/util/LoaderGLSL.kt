package vulkan.util

import com.google.common.io.Files
import game.input.LoaderFile
import graphics.scenery.spirvcrossj.*
import kool.ByteBuffer
import mu.KotlinLogging
import java.nio.ByteBuffer

class LoaderGLSL private constructor(val text: String, val stage: Int) {

    val logger = KotlinLogging.logger { }

    companion object {
        init {
            Loader.loadNatives()
            assert(libspirvcrossj.initializeProcess())
        }
        val resources: SWIGTYPE_p_TBuiltInResource = libspirvcrossj.getDefaultTBuiltInResource()

        fun ofVert(name: String) = LoaderGLSL(name, EShLanguage.EShLangVertex)
        fun ofFrag(name: String) = LoaderGLSL(name, EShLanguage.EShLangFragment)

        fun ofGLSL(path: String) = LoaderGLSL(LoaderFile.ofString("shaders\\glsl\\$path"),stageOf(path))

        /**
         * @param stage fill in manually if don't follow default file naming convention
         * */
        fun stageOf(path: String, stage: Int = -1) = when (path.substringAfterLast('.', "glsl")) {
            "vert" -> EShLanguage.EShLangVertex
            "frag" -> EShLanguage.EShLangFragment
            else -> stage
        }

        fun destroy() {
            libspirvcrossj.finalizeProcess()
        }
    }


    //    val text = LoaderFile.ofString("shaders\\glsl\\$name.${sufix(stage)}")

    val buffer: ByteBuffer

    init {

        val shader = TShader(stage)
        shader.setStrings(arrayOf(text), 1)
        shader.setAutoMapBindings(true)
        val messages = EShMessages.EShMsgDefault or EShMessages.EShMsgVulkanRules or EShMessages.EShMsgSpvRules
        assert(shader.parse(resources, 460, false, messages)) { "shader parse fail" }

        val program = TProgram()
        program.addShader(shader)
        assert(program.link(EShMessages.EShMsgDefault)) { "program link fail" }
        assert(program.mapIO()) { "program mapIO fail" }
        val spirv_intvec = IntVec()
        libspirvcrossj.glslangToSpv(program.getIntermediate(stage), spirv_intvec)
//        logger.info { "cap: ${spirv_intvec.capacity()} byte: " }
//        logger.info{ spirv_intvec.joinToString(separator = "_", transform = { l -> l.toString() }) }
//        logger.info{ spirv_intvec.joinToString(separator = "_", transform = { l -> l.toByte().toString() }) }

        buffer = ByteBuffer(spirv_intvec.size * Int.SIZE_BYTES)
        spirv_intvec.forEach{ buffer.putInt(it.toInt()) }
        buffer.flip()

    }

    fun sufix(stage: Int) = when (stage) {
        EShLanguage.EShLangVertex -> "vert"
        EShLanguage.EShLangFragment -> "frag"
        else -> "glsl"
    }

}