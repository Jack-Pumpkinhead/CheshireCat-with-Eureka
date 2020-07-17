package game.entity

import game.input.SpringInput
import game.main.Univ
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by CowardlyLion on 2020/6/3 21:12
 */
class Emeralds(val springInput: SpringInput) {

    val list = mutableListOf<Emerald>()
    val map = ConcurrentHashMap<String, Emerald>()


    private fun put(name: String): Emerald {
        val emerald = springInput.loadModel("model\\$name")!!
        list += emerald
        map[name] = emerald
        return emerald
    }

    fun get(name: String): Emerald? {
        return if (map.contains(name)) {
            map[name]
        } else {
            val model = springInput.loadModel("model\\$name")
            if (model == null) {
                Univ.logger.error {
                    "model not found: $name"
                }
                null
            } else {
                map[name] = model
                model
            }
        }
    }

    val crafting_table = put("crafting_table.dae")
    val englishCharacter = put("englishCharacter.dae")
    val icosphere = put("Icosphere.dae")
    val covid19 = put("Covid-19.dae")

    val mutex = Mutex()

    init {
        Univ.logger.info {
            "Emeralds loaded"
        }
    }



}