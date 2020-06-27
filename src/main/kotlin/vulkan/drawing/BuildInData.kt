package vulkan.drawing

/**
 * Created by CowardlyLion on 2020/6/17 18:02
 */
class BuildInData {

    companion object {

        val vcRect = floatArrayOf(
            -0.5f, -0.5f, +0f, 1f, 0f, 0f,
            +0.5f, -0.5f, +0f, 0f, 1f, 0f,
            +0.5f, +0.5f, +0f, 0f, 0f, 1f,
            -0.5f, +0.5f, +0f, 1f, 1f, 1f
        )

        val iRect = intArrayOf(
            0, 2,1, 3,2, 0      //迷之方向 counterclockwise
        )

    }

}