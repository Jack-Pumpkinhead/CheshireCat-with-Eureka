package game.main

import game.entity.Emeralds
import game.input.GLSLoader
import game.input.SpringInput
import vulkan.image.OzImages

/**
 * Created by CowardlyLion on 2020/4/20 14:22
 */
object OzConfigurations {
    val imageLoader = OzImages::class
    val modelLoader = Emeralds::class
    val glslLoader = GLSLoader::class
    val genericLoader = SpringInput::class

}