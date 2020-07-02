package game.event

import glm_.vec2.Vec2
import uno.glfw.MouseButton

/**
 * Created by CowardlyLion on 2020/6/28 18:37
 */
class MouseEnter()
class MouseExit()
class MouseMove(val pos: Vec2)
class MousePress(val button: MouseButton, val mods: Int)
class MouseRelease(val button: MouseButton, val mods: Int)
class MouseScroll(val delta: Float)


