package vulkan.concurrent

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import vkk.entities.VkImage
import vkk.identifiers.CommandBuffer

/**
 * Created by CowardlyLion on 2020/5/10 17:16
 */
class OzImage(val image: VkImage) {

    private val drawCmds = mutableListOf<CommandBuffer>()

    val mutex = Mutex()

    suspend fun add(cb: CommandBuffer) {
        mutex.withLock {
            drawCmds += cb
        }
    }
    suspend fun add(cb: Collection<CommandBuffer>) {
        mutex.withLock {
            drawCmds += cb
        }
    }
    suspend fun remove(cb: CommandBuffer) {
        mutex.withLock {
            drawCmds -= cb
        }
    }
    suspend fun remove(cb: Collection<CommandBuffer>) {
        mutex.withLock {
            drawCmds -= cb
        }
    }
    suspend fun getDrawCmds() = mutex.withLock {
        drawCmds.toTypedArray()
    }
    suspend fun clear() = mutex.withLock {
        drawCmds.clear()
    }
    suspend fun wait_clear(job: Job, cleared: CompletableJob) = mutex.withLock {
        job.join()
        drawCmds.clear()
        cleared.complete()
    }

    //no need to clear after swapchain recreated (destroyed at that time)


}