package game.input

import gli_.Texture
import gli_.gli
import vulkan.OzVulkan
import javax.imageio.ImageIO

/**
 * Created by CowardlyLion on 2020/5/24 22:13
 */
object LoadImages {

    fun load(name: String, flipY: Boolean = false): Texture {
        val image = gli.loadImage(ImageIO.read(LoadFile.inputStream(name)), flipY)
//        gli.makeTexture2d()
        val extent = image.extent(0)
        val levels = image.levels()
        return image
    }
}