package game.entity

import game.input.LoadModel
import game.input.SpringInput
import kotlinx.coroutines.runBlocking

/**
 * Created by CowardlyLion on 2020/6/3 21:12
 */
class Emeralds(val springInput: SpringInput) {
//    val crafting_table = runBlocking {
//        LoadModel.get("model\\crafting_table.dae")
//    }
    val crafting_table =springInput.get("model\\crafting_table.dae")



}