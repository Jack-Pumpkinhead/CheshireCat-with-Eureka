package math.matrix

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import mu.KotlinLogging
import org.springframework.beans.factory.getBean
import vkk.vk10.structs.Extent2D
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/5/2 22:37
 */
class MVP(val vulkan: OzVulkan) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    init {
//        glm.perspective()
    }

    var time = System.currentTimeMillis()

    fun getMatrix(): Mat4 {
        val extent2D = vulkan.swapchainContext.getBean<Extent2D>()
        val current = System.currentTimeMillis()
        val sec = (current - time) / 1000.toFloat()
        val model = glm_.glm.rotate(Mat4(), sec * glm_.glm.radians(90.0f), Vec3(0.0f, 0.0f, 1.0f))
        val view = glm_.glm.lookAt(Vec3(2.0f, 2.0f, 2.0f), Vec3(0.0f, 0.0f, 0.0f), Vec3(0.0f, 0.0f, 1.0f))
//        val projection = glm.perspective(glm.radians(45.0f), extent2D.width / extent2D.height.toFloat(), 0.1f, 10.0f)
        val projection = glm_.glm.perspective(glm_.glm.radians(45.0f), extent2D.width / extent2D.height.toFloat(), 0.1f, 10.0f)
        projection[1][1] *= -1  //GLM was originally designed for OpenGL, where the Y coordinate of the clip coordinates is inverted.

        return model.times(view).times(projection)
    }

}