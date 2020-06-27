package game.event

import game.main.SusAction
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Created by CowardlyLion on 2020/6/2 19:57
 */
class Event<T> {

    private val channel = Channel<T>(Channel.Factory.BUFFERED)  //why not broadcast channel?

    suspend fun send(element: T) = channel.send(element)


    private val subscriber = mutableListOf<SusAction<T>>()
    private val mutex = Mutex()

    suspend fun subscribe(action: SusAction<T>) {
        mutex.withLock {
            subscriber += action
        }
    }

    suspend fun process() {
        for (msg in channel) {
            mutex.withLock {
                subscriber.forEach {action->
                    action(msg)
                }
            }
        }
    }

}