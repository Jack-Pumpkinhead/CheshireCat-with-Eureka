package math

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import mu.KotlinLogging

/**
 * Created by CowardlyLion on 2020/5/7 22:21
 */
class Poset<T> {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val poset: MutableGraph<T> = GraphBuilder.directed().allowsSelfLoops(false).build()




}