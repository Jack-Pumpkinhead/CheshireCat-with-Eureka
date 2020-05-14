package vulkan.concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.selects.select
import vkk.VkCommandBufferLevel
import vkk.entities.VkCommandPool
import vkk.identifiers.CommandBuffer
import vkk.vk10.*
import vkk.vk10.structs.CommandBufferAllocateInfo
import vulkan.OzDevice

class OzCommandPool(
    val device: OzDevice,
    val commandPool: VkCommandPool
) {

    sealed class Action {

        class AllocateCB(
            val level: VkCommandBufferLevel = VkCommandBufferLevel.PRIMARY,
            val resp: CompletableDeferred<CommandBuffer>
        ) : Action()

        class AllocateCBs(
            val level: VkCommandBufferLevel = VkCommandBufferLevel.PRIMARY,
            val count: Int,
            val resp: CompletableDeferred<Array<CommandBuffer>>
        ) : Action()

        class FreeCB(val cb: CommandBuffer, val resp: CompletableJob) : Action()
        class Reset(val resp: CompletableJob) : Action()
        //Trimming may be an expensive operation, and should not be called frequently.
        class Trim(val resp: CompletableJob) : Action()
        class WaitComplete(val resp: CompletableJob) : Action()

    }


    suspend fun allocate(): CompletableDeferred<CommandBuffer> {
        val resp = CompletableDeferred<CommandBuffer>()
        actor.send(Action.AllocateCB(resp = resp))
        return resp
    }
    suspend fun allocate(size: Int): CompletableDeferred<Array<CommandBuffer>> {
        val resp = CompletableDeferred<Array<CommandBuffer>>()
        actor.send(Action.AllocateCBs(count = size, resp = resp))
        return resp
    }

    suspend fun free(cb: CommandBuffer): Job {
        val resp = Job()
        actor.send(Action.FreeCB(cb, resp))
        return resp
    }
    suspend fun reset(): Job {
        val resp = Job()
        actor.send(Action.Reset(resp))
        return resp
    }
    suspend fun trim(): Job {
        val resp = Job()
        actor.send(Action.Trim(resp))
        return resp
    }
    suspend fun waitComplete(): Job {
        val resp = Job()
        actor.send(Action.WaitComplete(resp))
        return resp
    }



    private val wait = TaskA()
    private val wait_reset = TaskAB()
    suspend fun wait(toWait: Job) = wait.wait(toWait)
    suspend fun wait_im(toWait: Job) = wait.wait(toWait).join()
    suspend fun wait_reset(toWait: Job) = wait_reset.wait_reset(toWait)
    suspend fun wait_reset_im(toWait: Job) = wait_reset.wait_reset_im(toWait)


    val actor = device.scope.actor<Action> {
        while (isActive) {
            select<Unit> {
                wait.channel.onReceive { (resp, toWait) ->
                    resp.complete()
                    toWait.join()
                }
                wait_reset.channel.onReceive { (resp, toWait, reseted) ->
                    resp.complete()
                    toWait.join()
                    var msg = channel.poll()
                    while (msg != null) {
                        when (msg) {
                            is Action.AllocateCB -> msg.resp.cancel()
                            is Action.AllocateCBs -> msg.resp.cancel()
                            is Action.FreeCB -> msg.resp.cancel()
                            is Action.Reset -> msg.resp.cancel()
                            is Action.Trim -> msg.resp.cancel()
                        }
                        msg = channel.poll()
                    }
                    device.device.resetCommandPool(commandPool)
                    device.device.trimCommandPool(commandPool, 0)
                    reseted.complete()
                }

                channel.onReceive {msg ->
                    when (msg) {
                        is Action.AllocateCB -> msg.resp.complete(
                            device.device.allocateCommandBuffer(CommandBufferAllocateInfo(commandPool, msg.level))
                        )
                        is Action.AllocateCBs -> msg.resp.complete(
                            device.device.allocateCommandBuffers(CommandBufferAllocateInfo(commandPool, msg.level, msg.count))
                        )
                        is Action.FreeCB -> {
                            device.device.freeCommandBuffers(commandPool, msg.cb)
                            msg.resp.complete()
                        }
                        is Action.Reset -> {
                            device.device.resetCommandPool(commandPool)
                            msg.resp.complete()
                        }
                        is Action.Trim -> {
                            device.device.trimCommandPool(commandPool, 0)
                            msg.resp.complete()
                        }
                        is Action.WaitComplete -> msg.resp.complete()
                    }
                }
            }
        }
    }



    fun destroy() {
//        actor.close()
        device.device.destroy(commandPool)
    }




}