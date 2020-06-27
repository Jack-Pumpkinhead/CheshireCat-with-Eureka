package vulkan.util

import game.input.LoadFile
import graphics.scenery.spirvcrossj.*
import kool.ByteBuffer
import java.nio.ByteBuffer

class LoaderGLSL private constructor() {

    companion object {
        init {
            Loader.loadNatives()
            assert(libspirvcrossj.initializeProcess())
        }
        val resources: SWIGTYPE_p_TBuiltInResource = libspirvcrossj.getDefaultTBuiltInResource()

        fun ofVert(name: String) = getBuffer(name, EShLanguage.EShLangVertex)
        fun ofFrag(name: String) = getBuffer(name, EShLanguage.EShLangFragment)

        fun ofGLSL(path: String) = getBuffer(LoadFile.ofString("shaders\\glsl\\$path"), stageOf(path))

        /**
         * @param stage fill in manually if don't follow default file naming convention
         * */
        fun stageOf(path: String, stage: Int = -1) = when (path.substringAfterLast('.', "glsl")) {
            "vert" -> EShLanguage.EShLangVertex
            "frag" -> EShLanguage.EShLangFragment
            else -> stage
        }

        fun sufixOf(stage: Int) = when (stage) {
            EShLanguage.EShLangVertex -> "vert"
            EShLanguage.EShLangFragment -> "frag"
            else -> "glsl"
        }

        fun getBuffer(text: String, stage: Int): ByteBuffer {
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

            val buffer = ByteBuffer(spirv_intvec.size * Int.SIZE_BYTES)
            spirv_intvec.forEach { buffer.putInt(it.toInt()) }
            buffer.flip()

            return buffer
        }

        fun destroy() {
            libspirvcrossj.finalizeProcess()
        }
    }





}