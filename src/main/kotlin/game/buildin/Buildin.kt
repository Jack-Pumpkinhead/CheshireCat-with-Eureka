package game.buildin

import game.main.Univ

/**
 * Created by CowardlyLion on 2020/7/22 13:34
 */
class Buildin(val univ: Univ) {
    lateinit var textureSets: BuildInTextureSets
    lateinit var mesh: BuildInMesh
    lateinit var dataVI: BuildInDataVI


    suspend fun loading() {
        textureSets = BuildInTextureSets(
            univ.vulkan.images,
            univ.vulkan.samplers,
            univ.vulkan.descriptorSets.singleTexture
        )
        mesh = BuildInMesh(univ)
        dataVI = BuildInDataVI(univ, this)
        dataVI.init()
    }


}