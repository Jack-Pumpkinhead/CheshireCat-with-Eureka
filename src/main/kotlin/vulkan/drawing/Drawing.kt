package vulkan.drawing

import game.loop.FrameLoop
import kotlinx.coroutines.runBlocking
import math.matrix.MVP_deprecated
import mu.KotlinLogging
import org.springframework.beans.factory.getBean
import vulkan.OzVulkan
@Deprecated("old")
class Drawing(val ozVulkan: OzVulkan, val frameLoop: FrameLoop) {

    companion object {

        val logger = KotlinLogging.logger { }

    }


    val simpleObject = SimpleObject(ozVulkan)

//    val triangle: OzVertexDataImmutable = simpleObject.getTriangle()
    val rectangle: OzVertexDataImmutable = simpleObject.getRectangle()
    //TODO: use OzObjects for management

    init {
        val ozObjects = ozVulkan.swapchainContext.getBean<OzObjects_deprecated>()
        runBlocking {
            ozObjects.register(OzObject_deprecated(rectangle, MVP_deprecated(ozVulkan)))
        }

    }

    var ii = 0

//    val drawImage = ozVulkan.swapchainContext.getBean<DrawImage>()
//    concurrent problem

    fun draw() {
        ii++
        val drawSuccess = ozVulkan.swapchainContext.getBean<DrawImage>().drawImage()
//        if (ii % 10000 == 0) {
//            logger.info {
//                "drawSuccess: $drawSuccess"
//            }
//        }
        if (!drawSuccess) {
            ozVulkan.shouldRecreate = true
        }
    }



    fun destroy() {
//        triangle.destroy()
        rectangle.destroy()
    }


}