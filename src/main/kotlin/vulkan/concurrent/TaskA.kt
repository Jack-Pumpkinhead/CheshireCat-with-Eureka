package vulkan.concurrent

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel

/**
 * Created by CowardlyLion on 2020/5/9 18:16
 */
class TaskA(val channel: Channel<Task> = Channel(Channel.BUFFERED)) {

    /*
    *   (A wait)   ----- (A running) ---- jobA(complete)
    *            /                            \
    *           /                              \
    *   respB(complete) ---- (B wait) ------       ------ (B running) ----
    * */
    data class Task(val respB: CompletableJob, val jobA: Job)

    suspend fun wait(toWait: Job): Job {
        val resp = Job()
        channel.send(Task(resp, toWait))
        return resp
    }

}