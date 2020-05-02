package vulkan.drawing

import game.loop.FrameLoop
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import vulkan.OzDevice
import vulkan.concurrent.OzFramebuffer
import vulkan.OzVulkan

class Drawing(val vulkan: OzVulkan, val device: OzDevice, val frameLoop: FrameLoop) {

    companion object {

        val logger = KotlinLogging.logger { }

    }


    val simpleObject = SimpleObject(vulkan)

    val rectangle: OzVertexDataImmutable = simpleObject.getRectangle()
//    val triangle: OzVertexDataImmutable = simpleObject.getTriangle()

    init {
        vulkan.after.add(rectangle::afterSwapchainRecreated)
//        vulkan.after.add(triangle::afterSwapchainRecreated)
    }

    var ii = 0

    fun draw() {
        ii++
//        r1.destroy()
//        r1 = simpleObject.getR(ii.toFloat() / 100000f)
//        r2.destroy()
//        r2 = simpleObject.getRR(ii.toFloat() / 100000f)

        val drawSuccess = device.graphicQ.drawImage()
//        if (ii % 10000 == 0) {
//            logger.info {
//                "drawSuccess: $drawSuccess"
//            }
//        }
        if (!drawSuccess) {
            vulkan.shouldRecreate = true
        }
    }


}