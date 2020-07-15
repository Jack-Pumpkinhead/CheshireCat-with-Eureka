package game.event

import glm_.vec2.Vec2
import uno.glfw.MouseButton

/**
 * Created by CowardlyLion on 2020/6/28 18:37
 */
class MouseEnter()
class MouseExit()
data class MouseMove(val pos: Vec2)
data class MousePress(val button: MouseButton, val mods: Int)
data class MouseRelease(val button: MouseButton, val mods: Int)
data class MouseScroll(val delta: Float)


