package vulkan.image

import vkk.entities.VkImageView
import vkk.vk10.structs.ImageCreateInfo

/**
 * Created by CowardlyLion on 2020/7/21 14:26
 */
class OzImage2(
    val createInfo: ImageCreateInfo,
    val image: VmaImage,
    val imageView: VkImageView
) {



}