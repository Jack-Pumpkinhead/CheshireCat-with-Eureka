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

    val windowResize = Event<WindowResize>()

    val keyPress = Event<KeyPress>()
    val keyRelease = Event<KeyRelease>()
    val mouseEnter    = Event<MouseEnter   >()
    val mouseExit     = Event<MouseExit    >()
    val mouseMove     = Event<MouseMove    >()
    val mousePress    = Event<MousePress   >()
    val mouseRelease  = Event<MouseRelease >()
    val mouseScroll   = Event<MouseScroll  >()

    val descripterSetUpdate = Event<DescripterSetUpdate>()


    val events = listOf(
        perSecond,
        afterRecreateSwapchain,
        onTick,
        onFrameStart,
        onFrameEnd,
        windowResize,
        keyPress,
        keyRelease,
        mouseEnter,
        mouseExit,
        mouseMove,
        mousePress,
        mouseRelease,
        mouseScroll,
        descripterSetUpdate
    )

    suspend fun launch(scope: CoroutineScope) {
        events.forEach {
            scope.launch { it.process() }
        }
    }
}