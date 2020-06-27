package game.loop

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging

@Deprecated("ddd")
sealed class TPSActor_old {

    class Record(val resp: CompletableDeferred<Long>) : TPSActor_old()
    class GetTPS(val resp: CompletableDeferred<Int>) : TPSActor_old()
    class GetTotal(val resp: CompletableDeferred<Long>) : TPSActor_old()

    companion object {

        private val logger = KotlinLogging.logger { }

        //TODO: integrate into TPSCounterConcurrent
        fun launch(coroutineScope: CoroutineScope) = coroutineScope.actor<TPSActor_old>(capacity = 64) {
            val tps = TPSCounter()
            for (msg in channel) {
                when (msg) {
                    is Record -> msg.resp.complete(tps.record())
                    is GetTPS -> msg.resp.complete(tps.getTPS())
                    is GetTotal -> msg.resp.complete(tps.counter)
                }
            }
        }

        suspend fun SendChannel<TPSActor_old>.record(): Long {
            val resp = CompletableDeferred<Long>()
            send(Record(resp))
            return resp.await()
        }
        suspend fun SendChannel<TPSActor_old>.getTPS(): Int {
            val resp = CompletableDeferred<Int>()
            send(GetTPS(resp))
            return resp.await()
        }
        suspend fun SendChannel<TPSActor_old>.getTotal(): Long {
            val resp = CompletableDeferred<Long>()
            send(GetTotal(resp))
            return resp.await()
        }



    }

}