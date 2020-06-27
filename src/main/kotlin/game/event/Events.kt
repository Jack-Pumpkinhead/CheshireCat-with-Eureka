package game.event

import glm_.vec2.Vec2i
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Created by CowardlyLion on 2020/6/2 19:56
 */
class Events {

//    val map = mutableMapOf<Event<>>()

    val perSecond = Event<Long>()
    val afterRecreateSwapchain = Event<Vec2i>()
    val onTick = Event<Tick>()

    val onFrameStart = Event<FrameTick>()
    val onFrameEnd = Event<FrameTick>()


    val events = listOf(
        perSecond,
        afterRecreateSwapchain,
        onTick,
        onFrameStart,
        onFrameEnd
    )

    suspend fun launch(scope: CoroutineScope) {
        events.forEach {
            scope.launch { it.process() }
        }
    }
}