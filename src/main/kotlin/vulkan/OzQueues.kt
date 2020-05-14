package vulkan

import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.Job
import vulkan.concurrent.OzQueue

/**
 * Created by CowardlyLion on 2020/5/8 21:33
 */
class OzQueues(val device: OzDevice) {

    //make sure queues are different
    val graphicQ: OzQueue = device.queueMap[device.graphicI]!!
    val presentQ = device.queueMap[device.presentI]!!
    val transferQ = device.queueMap[device.transferI]!!

    val qs = listOf(graphicQ, presentQ, transferQ)

    suspend fun onRecreateRenderpass(job: CompletableJob):List<Job> {
        return device.queueMap.values.map {
            it.wait_clear_im(job)
        }
    }


    fun destroy() {
        qs.forEach {
            it.destroy()
        }
        OzVulkan.logger.info {
            "${javaClass.name} destroyed"
        }
    }



}