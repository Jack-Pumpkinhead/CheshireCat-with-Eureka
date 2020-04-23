package game.loop.ticker

import game.loop.TPSActor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.isActive
import mu.KotlinLogging
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class FixRateAcc(
    val delayMillis: Long = 1000L,
    val initialDelayMillis: Long = 0,
    val context: CoroutineContext = EmptyCoroutineContext,
    val mode: TickerMode = TickerMode.FIXED_PERIOD
) {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    private val ticker = ticker(delayMillis, initialDelayMillis, context, mode)

    private val counter = BroadcastChannel<Long>(3)

    fun subscribe() = counter.openSubscription()

    suspend fun ticking() {
        var i = 0L
        while (context.isActive) {
            ticker.receive()
            counter.send(i)
            i++
        }
    }

}
