package vulkan.drawing

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vulkan.pipelines.pipelineLayout.OzUniformMatrixDynamic

/**
 * Created by CowardlyLion on 2020/5/12 19:52
 */
class OzObjects(val matrix: OzUniformMatrixDynamic) {

    val objects = mutableListOf<OzObject>()

    val mutex = Mutex()

    suspend fun register(a: OzObject) = mutex.withLock {
        objects += a
        objects.lastIndex
    }
    suspend fun unRegister(a: OzObject) = mutex.withLock {
        objects -= a
    }
    suspend fun getObjects()=mutex.withLock {
        objects.toList()
    }

    suspend fun getDrawCmds(imageIndex: Int) = mutex.withLock {
        objects.map {
            it.data.getCmd()[imageIndex]
        }.toTypedArray()
    }
    suspend fun refresh(imageIndex: Int) {
        matrix.matrixBuffers[imageIndex].resize(objects.size)
        objects.forEachIndexed { i, o ->
            matrix.matrixBuffers[imageIndex].set(
                i, o.mvp.getMatrix()
            )
            matrix.update(imageIndex)
        }
    }




}