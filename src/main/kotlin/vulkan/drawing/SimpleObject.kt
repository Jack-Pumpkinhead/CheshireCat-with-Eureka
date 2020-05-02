package vulkan.drawing

import kool.*
import mu.KotlinLogging
import vulkan.OzVulkan
import kotlin.random.Random

class SimpleObject(val ozVulkan: OzVulkan) {

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

        return OzVertexDataImmutable(ozVulkan.vma, ozVulkan.cb, vertices, indices)
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

        return OzVertexDataImmutable(ozVulkan.vma, ozVulkan.cb, vertices, indices)
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

        return OzVertexDataImmutable(ozVulkan.vma, ozVulkan.cb, vertices, indices)
    }

}