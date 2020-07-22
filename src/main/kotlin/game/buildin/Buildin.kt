package game.buildin

import game.main.Univ

/**
 * Created by CowardlyLion on 2020/7/22 13:34
 */
class Buildin(val univ: Univ) {
    lateinit var buildInTextureSets: BuildInTextureSets


    fun loading() {
        buildInTextureSets = BuildInTextureSets(
            univ.vulkan.images,
            univ.vulkan.samplers,
            univ.vulkan.descriptorSets.singleTexture
        )
    }


}