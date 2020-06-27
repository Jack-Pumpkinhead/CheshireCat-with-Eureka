package game.main

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

