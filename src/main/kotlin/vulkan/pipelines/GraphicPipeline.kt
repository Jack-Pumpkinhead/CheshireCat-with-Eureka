package vulkan.pipelines

import vkk.VkPipelineBindPoint
import vkk.entities.VkPipelineCache
import vkk.identifiers.CommandBuffer
import vkk.vk10.createGraphicsPipeline
import vkk.vk10.structs.GraphicsPipelineCreateInfo
import vulkan.OzDevice
import vulkan.concurrent.SyncArray2
import vulkan.drawing.DataVI

/**
 * Created by CowardlyLion on 2020/7/20 21:26
 */
class GraphicPipeline(val device: OzDevice, val createInfo: GraphicsPipelineCreateInfo) {

    val graphicsPipeline = device.device.createGraphicsPipeline(
        pipelineCache = VkPipelineCache.NULL,
        createInfo = createInfo
    )

    val arr = SyncArray2<DataVI>()

    suspend fun draw(cb: CommandBuffer, imageIndex: Int) {
        arr.forEachActive_ifHas(
            {
                cb.bindPipeline(
                    pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                    pipeline = graphicsPipeline
                )
            }, {
                it.draw(cb, imageIndex)
            }, {}
        )


    }

    fun destroy() {
        device.device.destroy(graphicsPipeline)
    }
}