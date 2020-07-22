package game.main

import game.entity.Emeralds
import game.input.GLSLoader
import game.input.SpringInput
import vkk.VkSampleCount
import vulkan.image.OzImages

/**
 * Created by CowardlyLion on 2020/4/20 14:22
 */
object OzConfigurations {
    private val imageLoader = OzImages::class
    private val modelLoader = Emeralds::class
    private val glslLoader = GLSLoader::class
    private val genericLoader = SpringInput::class

    var MSAA = VkSampleCount._1_BIT
    var textureGenerateMipmap = true




}