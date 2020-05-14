package vulkan.concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.selects.select
import vkk.VkResult
import vkk.extensions.PresentInfoKHR
import vkk.extensions.presentKHR
import vkk.identifiers.Queue
import vkk.vk10.resetFences
import vkk.vk10.structs.SubmitInfo
import vkk.vk10.submit
import vkk.vk10.waitForFences
import vulkan.OzDevice

class OzQueue(val device: OzDevice, val queue: Queue) {

    sealed class Action {
        class Submit(val info: SubmitInfo, val resp: CompletableDeferred<VkResult>) : Action()
        class Present(val info: PresentInfoKHR, val resp: CompletableDeferred<VkResult>) : Action()
        class WaitComplete(val resp: CompletableJob) : Action()
    }

    suspend fun submit(info: SubmitInfo): CompletableDeferred<VkResult> {
        val resp = CompletableDeferred<VkResult>()
        actor.send(Action.Submit(info, resp))
        return resp
    }

    suspend fun present(info: PresentInfoKHR): CompletableDeferred<VkResult> {
        val resp = CompletableDeferred<VkResult>()
        actor.send(Action.Present(info, resp))
        return resp
    }
    suspend fun waitComplete(): Job {
        val resp = Job()
        actor.send(Action.WaitComplete(resp))
        return resp
    }




    private val wait = TaskA()
    private val wait_clear = TaskAB()
    suspend fun wait(toWait: Job) = wait.wait(toWait)
    suspend fun wait_clear(toWait: Job) = wait_clear.wait_reset(toWait)
    suspend fun wait_clear_im(toWait: Job) = wait_clear.wait_reset_im(toWait)


    val fence = device.signaledFence()

    val actor = device.scope.actor<Action> {

        while (isActive) {
            select<Unit> {
                wait_clear.channel.onReceive { (resp, toWait, clear) -> //两次握手
                    resp.complete()
                    toWait.join()

                    var msg = channel.poll()
                    while (msg != null) {
                        when (msg) {
                            is Action.Submit -> msg.resp.cancel()
                            is Action.Present -> msg.resp.cancel()
                        }
                        msg = channel.poll()
                    }
                    clear.complete()
                }
                wait.channel.onReceive { (resp, toWait) ->
                    resp.complete()
                    toWait.join()
                }
                channel.onReceive {msg ->
                    when (msg) {
                        is Action.Submit -> {
                            device.device.resetFences(fence)
                            val result = queue.submit(msg.info, fence)
                            device.device.waitForFences(fence, true, -1)
                            msg.resp.complete(result)
                        }
                        is Action.Present -> {
                            val result = queue.presentKHR(msg.info)
                            msg.resp.complete(result)
                        }
                        is Action.WaitComplete -> msg.resp.complete()
                    }
                }
            }
        }
    }

    fun destroy() {
//        actor.close()
        device.device.destroy(fence)
    }



}