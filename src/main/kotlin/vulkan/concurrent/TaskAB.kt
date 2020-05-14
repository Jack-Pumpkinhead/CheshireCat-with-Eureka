package vulkan.concurrent

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel

/**
 * Created by CowardlyLion on 2020/5/9 18:26
 */
class TaskAB(val channel: Channel<Task> = Channel(Channel.BUFFERED)) {

    /*
    *   (A wait)   ----- (A running) ---- jobA(complete) --- (A wait) -----     ------ (A running) ----
    *            /                            \                             /
    *           /                              \                           /
    *   respB(complete) ---- (B wait) ------       ------ (B running) ----
    * */
    data class Task(val respB: CompletableJob, val jobA: Job, val respB2: CompletableJob)

    suspend fun wait_reset(toWait: Job): Pair<CompletableJob, CompletableJob> {
        val resp = Job()
        val reset = Job()
        channel.send(Task(resp, toWait, reset))
        return resp to reset
    }
    //wait immediately
    suspend fun wait_reset_im(toWait: Job):  CompletableJob {
        val resp = Job()
        val reset = Job()
        channel.send(Task(resp, toWait, reset))
        resp.join()
        return reset
    }


}