package game.loop

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging

sealed class TPSActor {

    object Record : TPSActor()
    class GetTPS(val resp: CompletableDeferred<Int>) : TPSActor()
    class GetTotal(val resp: CompletableDeferred<Long>) : TPSActor()

    companion object {

        private val logger = KotlinLogging.logger { }

        //TODO: integrate into TPSCounterConcurrent
        fun launch(coroutineScope: CoroutineScope) = coroutineScope.actor<TPSActor>(capacity = 64) {
            val tps = TPSCounter()
            for (msg in channel) {
                when (msg) {
                    Record -> tps.record()
                    is GetTPS -> msg.resp.complete(tps.getTPS())
                    is GetTotal -> msg.resp.complete(tps.counter)
                }
            }
        }

        suspend fun SendChannel<TPSActor>.record() {
            send(Record)
        }
        suspend fun SendChannel<TPSActor>.getTPS(): Int {
            val resp = CompletableDeferred<Int>()
            send(GetTPS(resp))
            return resp.await()
        }
        suspend fun SendChannel<TPSActor>.getTotal(): Long {
            val resp = CompletableDeferred<Long>()
            send(GetTotal(resp))
            return resp.await()
        }



    }

}