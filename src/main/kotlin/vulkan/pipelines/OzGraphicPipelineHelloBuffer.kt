package vulkan.pipelines

import glm_.vec4.Vec4
import vkk.*
import vkk.entities.*
import vkk.vk10.createGraphicsPipeline
import vkk.vk10.structs.*
import vulkan.OzDevice
import vulkan.OzRenderPasses
import vulkan.OzVulkan
import vulkan.pipelines.pipelineLayout.OzPipelineLayouts
import vulkan.pipelines.vertexInput.Vertex_p3c3

class OzGraphicPipelineHelloBuffer(
    val device: OzDevice,
    shadermodule: OzShaderModules,
    pipelineLayouts: OzPipelineLayouts,
    renderPasses: OzRenderPasses,
    subpass: Int = 0,
    extent2D: Extent2D
) {

    val graphicsPipeline: VkPipeline

    init {

        val shaderstageCI_vert = shadermodule.getPipelineShaderStageCI("hellobuffer.vert")
        val shaderstageCI_frag = shadermodule.getPipelineShaderStageCI("basic.frag")


        val inputAssemblyStateCI = PipelineInputAssemblyStateCreateInfo(
            topology = VkPrimitiveTopology.TRIANGLE_LIST,
            primitiveRestartEnable = false
        )

        val viewport = Viewport(
            x = 0.0f,
            y = 0.0f,
            width = extent2D.width.toFloat(),
            height = extent2D.height.toFloat(),
            minDepth = 0.0f,
            maxDepth = 1.0f
        )
        val scissor = Rect2D(
            offset = Offset2D(0, 0),
            extent = extent2D
        )

        val viewportSCI = PipelineViewportStateCreateInfo(
            viewports = arrayOf(viewport),
            scissors = arrayOf(scissor)
        )

        val rasterizationSCI = PipelineRasterizationStateCreateInfo(
            depthClampEnable = false,
            rasterizerDiscardEnable = false,
            polygonMode = VkPolygonMode.FILL,
            lineWidth = 1.0f,
            cullMode = VkCullMode.BACK_BIT.i,
            frontFace = VkFrontFace.CLOCKWISE,
            depthBiasEnable = false,
            depthBiasConstantFactor = 0.0f,
            depthBiasClamp = 0.0f,
            depthBiasSlopeFactor = 0.0f
        )

        val multisampleSCI = PipelineMultisampleStateCreateInfo(
            sampleShadingEnable = false,
            rasterizationSamples = VkSampleCount._1_BIT,
            minSampleShading = 1.0f,
            sampleMask = null,
            alphaToCoverageEnable = false,
            alphaToOneEnable = false
        )

        val colorBlendAttachmentState = PipelineColorBlendAttachmentState(
            colorWriteMask = VkColorComponent.R_BIT or VkColorComponent.G_BIT or VkColorComponent.B_BIT or VkColorComponent.A_BIT,
            blendEnable = false,
            srcColorBlendFactor = VkBlendFactor.ONE,
            dstColorBlendFactor = VkBlendFactor.ZERO,
            colorBlendOp = VkBlendOp.ADD,
            srcAlphaBlendFactor = VkBlendFactor.ONE,
            dstAlphaBlendFactor = VkBlendFactor.ZERO,
            alphaBlendOp = VkBlendOp.ADD
        )

        val colorBlendSCI = PipelineColorBlendStateCreateInfo(
            logicOpEnable = false,
            logicOp = VkLogicOp.COPY,
            attachments = arrayOf(colorBlendAttachmentState),
            blendConstants = Vec4(0.0f, 0.0f, 0.0f, 0.0f)
        )


        val graphicsPipelineCI = GraphicsPipelineCreateInfo(
            stages = arrayOf(shaderstageCI_vert, shaderstageCI_frag),
            vertexInputState = Vertex_p3c3.inputState,
            inputAssemblyState = inputAssemblyStateCI,
            viewportState = viewportSCI,
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI,
            depthStencilState = null,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = pipelineLayouts.empty,
            renderPass = renderPasses.renderpass,
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
        OzVulkan.logger.debug {
            "pipeline 'hellobuffer' destroyed"
        }
    }
}