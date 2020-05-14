package math.cat

import com.google.common.graph.GraphBuilder
import com.google.common.graph.MutableGraph
import com.google.common.graph.Traverser
import game.main.CleanUpMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging

/**
 * Created by CowardlyLion on 2020/5/8 11:23
 */
class CleanupActor {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    val cleanups: MutableGraph<CleanUpMethod> = GraphBuilder.directed().allowsSelfLoops(false).build<CleanUpMethod>()

    val scope = CoroutineScope(Dispatchers.Default)

    val actor = scope.actor<Action> {

    }

    private fun cleanup(c: CleanUpMethod) {
        if (cleanups.nodes().contains(c)) {
            val unders = mutableSetOf<CleanUpMethod>()
            Traverser.forGraph(cleanups).depthFirstPostOrder(c).forEach { it.invoke(); unders += it }
            unders.forEach{ cleanups.removeNode(it)}
        }
    }

    sealed class Action {

    }

}