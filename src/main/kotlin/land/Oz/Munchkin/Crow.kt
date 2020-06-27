package land.Oz.Munchkin

import game.Primitive
import game.main.Univ

/**
 * Created by CowardlyLion on 2020/6/27 22:40
 */
class Crow(univ: Univ) : Primitive(univ) {
    override suspend fun initialize() {
        Univ.logger.info {
            "Crow"
        }
    }


}