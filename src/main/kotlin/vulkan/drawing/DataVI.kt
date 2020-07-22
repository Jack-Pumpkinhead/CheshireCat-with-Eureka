package vulkan.drawing

import vkk.identifiers.CommandBuffer
import vulkan.buffer.IndexData
import vulkan.buffer.VertexData
import vulkan.concurrent.SyncArray2
import vulkan.command.BindDescriptorSets

/**
 * Created by CowardlyLion on 2020/7/21 17:16
 */
class DataVI(
    val vertex: VertexData,
    val index: IndexData
) {
    fun bind(cb: CommandBuffer) {
        vertex.bind(cb)
        index.bind(cb)
    }

    suspend fun draw(cb: CommandBuffer, imageIndex: Int) {
        descriptors.forEachActive_ifHas(
            { bind(cb) },
            {
                it.bind(cb, imageIndex)
                index.draw(cb)
            },
            {}
        )
    }

    val descriptors = SyncArray2<BindDescriptorSets>()

    fun destroy() {
        vertex.destroy()
        index.destroy()
    }


}