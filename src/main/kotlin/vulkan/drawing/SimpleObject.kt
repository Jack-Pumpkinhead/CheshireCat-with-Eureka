package vulkan.drawing

import mu.KotlinLogging
import org.springframework.beans.factory.getBean
import vulkan.OzVulkan
import vulkan.buffer.OzVMA
import kotlin.random.Random

class SimpleObject(val vulkan: OzVulkan) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    fun getTriangle(): OzVertexDataImmutable {
        val vertices = floatArrayOf(
            +0.0f, -0.5f, +0f, 1f, 0f, 0f,
            +0.5f, +0.5f, +0f, 0f, 1f, 0f,
            -0.5f, +0.5f, +0f, 0f, 0f, 1f
        )

        val indices = intArrayOf(
            0, 1, 2
        )

//        return OzVertexDataImmutable(ozVulkan.vma, ozVulkan.cb, vertices, indices)
        return OzVertexDataImmutable(
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vertices, indices, 0, vulkan
        )
    }
    fun getRectangle(): OzVertexDataImmutable {
        val vertices = floatArrayOf(
            -0.5f, -0.5f, +0f, 1f, 0f, 0f,
            +0.5f, -0.5f, +0f, 0f, 1f, 0f,
            +0.5f, +0.5f, +0f, 0f, 0f, 1f,
            -0.5f, +0.5f, +0f, 1f, 1f, 1f
        )

        val indices = intArrayOf(
            0, 1, 2, 2, 3, 0
        )

//        return OzVertexDataImmutable(vulkan.vma, vulkan.cb, vertices, indices)
        return OzVertexDataImmutable(
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vertices, indices, 0, vulkan
        )
    }


    fun getRandom():OzVertexDataImmutable{
        val vcount = Random.nextInt(100)

        val vertices = FloatArray(vcount * 6) {
            if (it % 6 / 3 == 0) {
                Random.nextFloat() * 2 - 1
            } else {
                Random.nextFloat()
            }
        }

        /*for (i in 0 until vcount * 6) {
            print("%.2f".format(vertices[i]) + "f, ")
        }*/

        val indices = IntArray(vcount * 2){
            Random.nextInt(vcount)
        }

        /*for (i in 0 until vcount * 2) {
            print("${indices[i]}, ")
        }
        println()
*/

//        return OzVertexDataImmutable(vulkan.vma, vulkan.cb, vertices, indices)
        return OzVertexDataImmutable(
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vulkan.swapchainContext.getBean(),
            vertices, indices, 2, vulkan
        )
    }

}