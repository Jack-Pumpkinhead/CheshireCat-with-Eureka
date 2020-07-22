package vulkan.pipelines

import game.event.Events
import game.main.Recorder3
import game.main.Univ
import kool.BYTES
import kool.Stack
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vkk.VkIndexType
import vkk.VkPipelineBindPoint
import vkk.entities.*
import vkk.identifiers.CommandBuffer
import vkk.vk10.bindDescriptorSets
import vkk.vk10.bindVertexBuffers
import vkk.vk10.createGraphicsPipeline
import vkk.vk10.structs.Extent2D
import vkk.vk10.structs.GraphicsPipelineCreateInfo
import vulkan.OzDevice
import vulkan.OzRenderPasses
import vulkan.OzVulkan
import vulkan.buffer.OzVMA
import vulkan.buffer.VmaBuffer
import vulkan.command.CopyBuffer
import vulkan.drawing.OzObjectTextured2
import vulkan.drawing.StaticObject
import vulkan.pipelines.descriptor.LayoutMVP
import vulkan.pipelines.descriptor.TextureSets
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.VertexInput

/**
 * Created by CowardlyLion on 2020/6/3 19:23
 */
class PipelineTextured(
    val device: OzDevice,
    shadermodule: OzShaderModules,
    pipelineLayouts: OzPipelineLayouts,
    renderPasses: OzRenderPasses,
    subpass: Int = 0,
    extent2D: Extent2D
) {

    val graphicsPipeline: VkPipeline
    val layout = pipelineLayouts.mvp_sampler

    init {


        val graphicsPipelineCI = GraphicsPipelineCreateInfo(
            stages = arrayOf(
                shadermodule.getPipelineShaderStageCI("hellosampler2.vert"),
                shadermodule.getPipelineShaderStageCI("hellosampler2.frag")
            ),
            vertexInputState = VertexInput.P3T2,
            inputAssemblyState = TriangleList,
            viewportState = viewportState(extent2D),
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI_MSAA,
            depthStencilState = depthStencilState,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = layout,
            renderPass = renderPasses.renderpass_depth_MSAA,
            subpass = subpass,
            basePipelineHandle = VkPipeline.NULL,
            basePipelineIndex = -1
        )

        graphicsPipeline = device.device.createGraphicsPipeline(
            pipelineCache = VkPipelineCache.NULL,
            createInfo = graphicsPipelineCI
        )

    }



    fun destroy() {
        device.device.destroy(graphicsPipeline)
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }

    class ObjStatic(
        val vma: OzVMA,
        val copyBuffer: CopyBuffer,
        var pipeline:PipelineTextured,
        val layoutMVP: LayoutMVP,
        val pos_texCoord: FloatArray,
        val indices: IntArray,
        var matrixIndex: Int,
        var texIndex:Int,
        val textureSets: TextureSets,
        events: Events
    ) {
        constructor(
            univ: Univ, pos_texCoord: FloatArray,
            indices: IntArray,
            matrixIndex: Int,
            texIndex: Int
        ) : this(
            vma = univ.vulkan.vma,
            pipeline = univ.vulkan.graphicPipelines.hellotexture,
            copyBuffer = univ.vulkan.copybuffer,
            layoutMVP = univ.vulkan.layoutMVP,
            pos_texCoord = pos_texCoord,
            indices = indices,
            matrixIndex = matrixIndex,
            texIndex = texIndex,
            textureSets = univ.vulkan.textureSets,
            events = univ.events
        )
        init {
            runBlocking {
                events.afterRecreateSwapchain.subscribe { (vulkan, extent) ->
                    pipeline = vulkan.graphicPipelines.hellotexture
                }
            }
        }


        val vbytes = pos_texCoord.size * Float.BYTES
        val ibytes = indices.size * Int.BYTES


        val vertexBuffer_device_local: VmaBuffer
        val indexBuffer_device_local: VmaBuffer

        init {

            val vertexBuffer = vma.createBuffer_vertexStaging(vbytes)
            val indexBuffer = vma.createBuffer_indexStaging(ibytes)

            Stack {
                vertexBuffer.memory.fill(
                    it.mallocFloat(pos_texCoord.size).put(pos_texCoord).flip()
                )
                indexBuffer.memory.fill(
                    it.mallocInt(indices.size).put(indices).flip()
                )
            }


            vertexBuffer_device_local = vma.of_VertexBuffer_device_local(vbytes)
            indexBuffer_device_local = vma.of_IndexBuffer_device_local(ibytes)

            runBlocking {
                copyBuffer.copyBuffer(vertexBuffer.pBuffer, vertexBuffer_device_local.pBuffer, vbytes)
                copyBuffer.copyBuffer(indexBuffer.pBuffer, indexBuffer_device_local.pBuffer, ibytes)
            }
            vertexBuffer.destroy()
            indexBuffer.destroy()

        }






        val recorder: Recorder3 = { cb,imageIndex ->
            cb.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline.graphicsPipeline
            )
            cb.bindVertexBuffers(
                firstBinding = 0,
                bindingCount = 1,
                buffers = VkBuffer_Array(listOf(vertexBuffer_device_local.vkBuffer)),
                offsets = VkDeviceSize_Array(listOf(VkDeviceSize(0)))
            )

            cb.bindIndexBuffer(
                buffer = indexBuffer_device_local.vkBuffer,
                offset = VkDeviceSize(0),
                indexType = VkIndexType.UINT32
            )

            cb.bindDescriptorSets(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                layout = pipeline.layout,
                firstSet = 0,
                descriptorSets = VkDescriptorSet_Array(
                    listOf(layoutMVP.sets[imageIndex], textureSets.sets[texIndex])
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

    }


    class MultiObject(
        var pipeline: VkPipeline,
        val pipelineLayout: VkPipelineLayout,
        val data: StaticObject,
        val layoutMVP: LayoutMVP,
        val textureSets: TextureSets
    ){
        val objs = mutableListOf<OzObjectTextured2>()

        val mutex = Mutex()

        suspend fun record(cb: CommandBuffer, imageIndex: Int) {
            cb.bindPipeline(
                pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                pipeline = pipeline
            )
            data.bind(cb)
            mutex.withLock {
                objs.forEach {obj->
                    cb.bindDescriptorSets(
                        pipelineBindPoint = VkPipelineBindPoint.GRAPHICS,
                        layout = pipelineLayout,
                        firstSet = 0,
                        descriptorSets = VkDescriptorSet_Array(
                            listOf(layoutMVP.sets[imageIndex], textureSets.sets[obj.texIndex])
                        ),
                        dynamicOffsets = intArrayOf(obj.model.index * layoutMVP.model.dynamicAlignment.toInt())
                    )
                    data.draw(cb)
                }
            }
        }

    }



}

