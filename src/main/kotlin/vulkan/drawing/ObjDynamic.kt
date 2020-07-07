package vulkan.drawing

import kool.BYTES
import kool.Stack
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vkk.VkIndexType
import vkk.VkPipelineBindPoint
import vkk.entities.*
import vkk.identifiers.CommandBuffer
import vkk.vk10.bindDescriptorSets
import vkk.vk10.bindVertexBuffers
import vulkan.buffer.OzVMA
import vulkan.pipelines.descriptor.LayoutMVP
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts

/**
 * Created by CowardlyLion on 2020/7/6 22:53
 */
class ObjDynamic (
    val vma: OzVMA,
    val layoutMVP: LayoutMVP,
    val layouts: OzPipelineLayouts,
    var pipeline: VkPipeline,
    var vert_color_etc: FloatArray,
    var indices: IntArray,
    var matrixIndex: Int
) {
    var vbytes = vert_color_etc.size * Float.BYTES
    var ibytes = indices.size * Int.BYTES

    var vertexBuffer = vma.vertexBuffer(vbytes)
    var indexBuffer = vma.indexBuffer(ibytes)


    val mutex = Mutex()
    var changed = false

    init {


        Stack { mStack ->
            vertexBuffer.memory.fill(
                mStack.mallocFloat(vert_color_etc.size).put(vert_color_etc).flip()
            )
            indexBuffer.memory.fill(
                mStack.mallocInt(indices.size).put(indices).flip()
            )
        }

    }

    suspend fun update(vert_color: FloatArray) {
        mutex.withLock {
            this.vert_color_etc = vert_color
            changed = true
        }
    }
    suspend fun update(indices: IntArray) {
        mutex.withLock {
            this.indices = indices
            changed = true
        }
    }
    suspend fun update(vert_color: FloatArray, indices: IntArray) {
        mutex.withLock {
            this.vert_color_etc = vert_color
            this.indices = indices
            changed = true
        }
    }


    //call from frameloop
    suspend fun update() {

        mutex.withLock {
            if (changed) {
                changed = false

                val vbytes = vert_color_etc.size * Float.BYTES
                val ibytes = indices.size * Int.BYTES

                if (this.vbytes < vbytes) {
                    vertexBuffer.destroy()
                    vertexBuffer = vma.vertexBuffer(vbytes)
                }
                if (this.ibytes < ibytes) {
                    indexBuffer.destroy()
                    indexBuffer = vma.indexBuffer(ibytes)
                }

                this.vbytes = vbytes
                this.ibytes = ibytes


                Stack { mStack ->
                    vertexBuffer.memory.fill(
                        mStack.mallocFloat(vert_color_etc.size).put(vert_color_etc).flip()
                    )
                    indexBuffer.memory.fill(
                        mStack.mallocInt(indices.size).put(indices).flip()
                    )
                }
            }
        }
    }


    fun record(cb: CommandBuffer, imageIndex: Int) {
        cb.bindPipeline(
            pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
            pipeline = pipeline
        )
        cb.bindVertexBuffers(
            firstBinding = 0,
            bindingCount = 1,
            buffers = VkBuffer_Array(listOf(vertexBuffer.vkBuffer)),
            offsets = VkDeviceSize_Array(listOf(VkDeviceSize(0)))
        )

        cb.bindIndexBuffer(
            buffer = indexBuffer.vkBuffer,
            offset = VkDeviceSize(0),
            indexType = VkIndexType.UINT32
        )

        cb.bindDescriptorSets(
            pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
            layout = layouts.mvp,
            firstSet = 0,
            descriptorSets = VkDescriptorSet_Array(
                listOf(layoutMVP.sets[imageIndex])
            ),
            dynamicOffsets = intArrayOf(matrixIndex * layoutMVP.model.dynamicAlignment.toInt())
        )

        cb.drawIndexed(
            indexCount = indices.size,
            instanceCount = 1,
            firstIndex = 0,
            vertexOffset = 0,
            firstInstance = 0
        )
    }

    suspend fun destroy() {
        mutex.withLock {
            vertexBuffer.destroy()
            indexBuffer.destroy()
        }
    }

}