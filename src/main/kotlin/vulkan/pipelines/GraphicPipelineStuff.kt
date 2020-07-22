package vulkan.pipelines

import game.main.OzConfigurations
import glm_.vec4.Vec4
import vkk.*
import vkk.vk10.structs.*

/**
 * Created by CowardlyLion on 2020/6/3 19:25
 */

val TriangleList = PipelineInputAssemblyStateCreateInfo(
    topology = VkPrimitiveTopology.TRIANGLE_LIST,
    primitiveRestartEnable = false
)
val LineList = PipelineInputAssemblyStateCreateInfo(
    topology = VkPrimitiveTopology.LINE_LIST,
    primitiveRestartEnable = false
)

fun viewport(extent2D: Extent2D) = Viewport(
    x = 0.0f,
    y = 0.0f,
    width = extent2D.width.toFloat(),
    height = extent2D.height.toFloat(),
    minDepth = 0.0f,
    maxDepth = 1.0f
)
fun scissor(extent2D: Extent2D) = Rect2D(
    offset = Offset2D(0, 0),
    extent = extent2D
)
fun viewportState(extent2D: Extent2D) =
    PipelineViewportStateCreateInfo(
        viewports = arrayOf(viewport(extent2D)),
        scissors = arrayOf(scissor(extent2D))
    )
val rasterizationSCI = PipelineRasterizationStateCreateInfo(
    depthClampEnable = false,
    rasterizerDiscardEnable = false,
    polygonMode = VkPolygonMode.FILL,
    lineWidth = 1.0f,
    cullMode = VkCullMode.BACK_BIT.i,
//    cullMode = VkCullMode.NONE.i,
    frontFace = VkFrontFace.COUNTER_CLOCKWISE,
//    frontFace = VkFrontFace.CLOCKWISE,
    depthBiasEnable = false,
    depthBiasConstantFactor = 0.0f,
    depthBiasClamp = 0.0f,
    depthBiasSlopeFactor = 0.0f
)
val multisampleSCI = PipelineMultisampleStateCreateInfo(
    sampleShadingEnable = false,
    rasterizationSamples = VkSampleCount._1_BIT,
//    rasterizationSamples = OzConfigurations.MSAA,
    minSampleShading = 1.0f,
    sampleMask = null,
    alphaToCoverageEnable = false,
    alphaToOneEnable = false
)
val multisampleSCI_MSAA = PipelineMultisampleStateCreateInfo(
    sampleShadingEnable = true,
    rasterizationSamples = OzConfigurations.MSAA,
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

val depthStencilState = PipelineDepthStencilStateCreateInfo(
    depthTestEnable = true,
    depthWriteEnable = true, //false for transparent object (允许更远的透明物体绘制)
    depthCompareOp = VkCompareOp.LESS,
//    depthCompareOp = VkCompareOp.NEVER,
    depthBoundsTestEnable = false,  //忽然正常? //见OzFramebuffer
    minDepthBounds = 0f,
    maxDepthBounds = 1f,
    stencilTestEnable = false,
    back = StencilOpState(),
    front = StencilOpState()
)
