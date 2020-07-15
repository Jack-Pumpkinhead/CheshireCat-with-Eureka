package vulkan.drawing

import kotlinx.coroutines.sync.Mutex

/**
 * Created by CowardlyLion on 2020/7/13 23:39
 */
class OzObjects() {
    val textured = mutableListOf<OzObjectTextured>()

    val mutex = Mutex()

}