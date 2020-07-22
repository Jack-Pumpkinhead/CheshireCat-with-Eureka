package vulkan.command

import vkk.identifiers.CommandBuffer

/**
 * Created by CowardlyLion on 2020/7/21 20:14
 */
interface BindDescriptorSets {
    fun bind(cb: CommandBuffer, imageIndex: Int)
}