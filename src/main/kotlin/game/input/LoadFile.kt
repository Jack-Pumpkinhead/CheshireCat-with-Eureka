package game.input

import mu.KotlinLogging

object LoadFile {
    val logger = KotlinLogging.logger { }


    fun inputStream(name: String) = javaClass.classLoader.getResourceAsStream(name)!!
    fun url(name: String) = javaClass.classLoader.getResource(name)!!



    fun ofString(name: String) = inputStream(name).readAllBytes().toString(Charsets.UTF_8)



}