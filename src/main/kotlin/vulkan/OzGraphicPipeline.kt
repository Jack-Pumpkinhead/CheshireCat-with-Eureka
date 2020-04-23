package vulkan

import glm_.vec4.Vec4
import mu.KotlinLogging
import vkk.*
import vkk.entities.*
import vkk.vk10.createGraphicsPipeline
import vkk.vk10.createPipelineLayout
import vkk.vk10.createShaderModule
import vkk.vk10.structs.*
import vulkan.util.LoaderGLSL

class OzGraphicPipeline(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val renderPass: OzRenderPass,
    val swapchain: OzSwapchain
) {

    val logger = KotlinLogging.logger { }

    val pipelineLayout: VkPipelineLayout
    val graphicsPipelines: VkPipeline_Array
    init {
        val vertCI = ShaderModuleCreateInfo(code = LoaderGLSL.ofVert("hellotriangle").buffer)
        val vert = device.device.createShaderModule(vertCI)
        val fragCI = ShaderModuleCreateInfo(code = LoaderGLSL.ofFrag("hellotriangle").buffer)
        val frag: VkShaderModule = device.device.createShaderModule(fragCI)

        assert(vert.isValid)
        assert(frag.isValid)

        val shaderstageCI_vert = PipelineShaderStageCreateInfo(
            stage = VkShaderStage.VERTEX_BIT,
            module = vert,
            name = "main"   //standard entry point
        )

        val shaderstageCI_frag = PipelineShaderStageCreateInfo(
            stage = VkShaderStage.FRAGMENT_BIT,
            module = frag,
            name = "main"
        )


        val vertexInputStateCI = PipelineVertexInputStateCreateInfo(
            vertexBindingDescriptions = null,
            vertexAttributeDescriptions = null
        )

        val inputAssemblyStateCI = PipelineInputAssemblyStateCreateInfo(
            topology = VkPrimitiveTopology.TRIANGLE_LIST,
            primitiveRestartEnable = false
        )

        val viewport = Viewport(
            x = 0.0f,
            y = 0.0f,
            width = swapchain.extent.width.toFloat(),
            height = swapchain.extent.height.toFloat(),
            minDepth = 0.0f,
            maxDepth = 1.0f
        )
        val scissor = Rect2D(
            offset = Offset2D(0, 0),
            extent = swapchain.extent
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

        pipelineLayout = device.device.createPipelineLayout(
            createInfo = PipelineLayoutCreateInfo(
                setLayouts = null,
                pushConstantRanges = null
            )
        )
        assert(pipelineLayout.isValid)


        val graphicsPipelineCI = GraphicsPipelineCreateInfo(
            stages = arrayOf(shaderstageCI_vert, shaderstageCI_frag),
            vertexInputState = vertexInputStateCI,
            inputAssemblyState = inputAssemblyStateCI,
            viewportState = viewportSCI,
            rasterizationState = rasterizationSCI,
            multisampleState = multisampleSCI,
            depthStencilState = null,
            colorBlendState = colorBlendSCI,
            dynamicState = null,
            layout = pipelineLayout,
            renderPass = renderPass.renderpass,
            subpass = 0,
            basePipelineHandle = VkPipeline.NULL,
            basePipelineIndex = -1
        )

        graphicsPipelines = device.device.createGraphicsPipeline(
            pipelineCache = VkPipelineCache.NULL,
            createInfos = arrayOf(graphicsPipelineCI)
        )

        graphicsPipelines.indices.forEach {
            assert(graphicsPipelines[it].isValid) { "graphic pipeline $it is invalid!"}
        }

        device.device.destroy(vert)
        device.device.destroy(frag)
    }


    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(renderPass::destroy, this::destroy)
    }

    fun destroy() {
        graphicsPipelines.indices.forEach {
            device.device.destroy(graphicsPipelines[it])
        }
        device.device.destroy(pipelineLayout)
    }
}