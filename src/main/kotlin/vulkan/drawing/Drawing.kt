package vulkan.drawing

import game.loop.FrameLoop
import mu.KotlinLogging
import vulkan.OzDevice
import vulkan.OzVulkan

class Drawing(val ozVulkan: OzVulkan, val device: OzDevice, val frameLoop: FrameLoop) {

    companion object {

        val logger = KotlinLogging.logger { }

    }


    val simpleObject = SimpleObject(ozVulkan)

//    val triangle: OzVertexDataImmutable = simpleObject.getTriangle()
    val rectangle: OzVertexDataImmutable = simpleObject.getRectangle()



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
            ozVulkan.shouldRecreate = true
        }
    }


    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(ozVulkan.vma::destroy, this::destroy)
    }

    fun destroy() {
//        triangle.destroy()
        rectangle.destroy()
    }


}