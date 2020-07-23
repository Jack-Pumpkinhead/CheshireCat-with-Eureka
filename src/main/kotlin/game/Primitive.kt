package game

import game.main.Univ

/**
 * Created by CowardlyLion on 2020/6/26 21:22
 */
open class Primitive(val univ: Univ) {

    var instantiate = true
    var active = true

    open suspend fun initialize() {

    }

    open suspend fun gameloop(tick: Long, timemillis: Long) {

    }

    //before update descriptor set
    open suspend fun updateBuffer(imageIndex: Int) {

    }

    open suspend fun destroy() {

    }

}