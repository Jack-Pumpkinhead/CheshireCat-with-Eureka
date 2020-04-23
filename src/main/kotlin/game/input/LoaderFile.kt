package game.input

import mu.KotlinLogging

object LoaderFile {
    val logger = KotlinLogging.logger { }


    fun ofString(name: String) =
        javaClass.classLoader.getResourceAsStream(name)!!
            .readAllBytes().toString(Charsets.UTF_8)


}