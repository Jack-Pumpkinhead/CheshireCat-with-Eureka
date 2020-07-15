package game.event

import uno.glfw.Key

/**
 * Created by CowardlyLion on 2020/6/28 18:23
 */
data class KeyPress(val key: Key, val mods: Int) {}
data class KeyRelease(val key: Key, val mods: Int) {}
