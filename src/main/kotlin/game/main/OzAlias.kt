package game.main

import glm_.vec3.Vec3
import physics.SingleParticle
import vkk.entities.VkDescriptorSet
import vkk.identifiers.CommandBuffer
import vulkan.util.DMDescriptors

/**
 * Created by CowardlyLion on 2020/4/20 18:08
 */

typealias CleanUpMethod = () -> Unit
typealias AfterSwapchainRecreated = suspend () -> Unit
typealias SusAction<T> = suspend (T) -> Unit
typealias Recorder = (CommandBuffer) -> Unit
typealias Recorder2 = (CommandBuffer, DMDescriptors) -> Unit
typealias Recorder3 = (CommandBuffer, Int) -> Unit

typealias ForceField = (Vec3, Vec3) -> Vec3
typealias Force = (SingleParticle) -> Vec3

typealias TickTimeAction = suspend (Long, Long) -> Unit
