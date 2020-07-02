package game.entity

import com.google.common.graph.GraphBuilder

/**
 * Created by CowardlyLion on 2020/6/1 22:11
 */
class Emerald {
    val structure = GraphBuilder.directed().allowsSelfLoops(false).build<EntityNode>()



    companion object {
        val NULL = Emerald()
    }


}