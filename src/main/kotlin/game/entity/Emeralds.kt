package game.entity

import game.input.LoadModel
import game.input.SpringInput
import game.main.Univ
import kotlinx.coroutines.runBlocking

/**
 * Created by CowardlyLion on 2020/6/3 21:12
 */
class Emeralds(val springInput: SpringInput) {

    val crafting_table = springInput.loadModel("model\\crafting_table.dae")


    val list = listOf(crafting_table)

    init {
        Univ.logger.info {
            "aaa"
        }
    }



}