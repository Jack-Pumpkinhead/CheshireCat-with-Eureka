package game.event

import glm_.vec2.Vec2i
import vulkan.OzVulkan

/**
 * Created by CowardlyLion on 2020/7/15 21:33
 */
data class SwapchainRecreated(val vulkan: OzVulkan, val extent: Vec2i) {}