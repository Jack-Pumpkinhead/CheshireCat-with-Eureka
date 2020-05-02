package vulkan.concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.selects.select
import mu.KotlinLogging
import vkk.VkCommandBufferLevel
import vkk.VkCommandPoolCreate
import vkk.VkCommandPoolCreateFlags
import vkk.entities.VkCommandPool
import vkk.identifiers.CommandBuffer
import vkk.identifiers.Device
import vkk.vk10.*
import vkk.vk10.structs.CommandBufferAllocateInfo
import vkk.vk10.structs.CommandPoolCreateInfo
import vulkan.OzDevice
import vulkan.OzVulkan

class OzCommandPool(
    val ozVulkan: OzVulkan,
    val device: OzDevice,
    val queueFamilyIndex: Int,
    val flags: VkCommandPoolCreateFlags = VkCommandPoolCreate(0).i
) {

    sealed class Action {

        abstract operator fun invoke(device: Device, commandPool: VkCommandPool)

        class AllocateCommandBuffer(
            val level: VkCommandBufferLevel = VkCommandBufferLevel.PRIMARY,
            val resp: CompletableDeferred<CommandBuffer>
        ) : Action() {
            override fun invoke(device: Device, commandPool: VkCommandPool) {
                resp.complete(
                    device.allocateCommandBuffer(CommandBufferAllocateInfo(commandPool, level))
                )
            }
        }
        class AllocateCommandBuffers(
            val level: VkCommandBufferLevel = VkCommandBufferLevel.PRIMARY,
            val size: Int,
            val resp: CompletableDeferred<Array<CommandBuffer>>
        ) : Action() {
            override fun invoke(device: Device, commandPool: VkCommandPool) {
                resp.complete(
                    device.allocateCommandBuffers(CommandBufferAllocateInfo(commandPool, level, size))
                )
            }
        }


        class FreeCommandBuffer(val cb: CommandBuffer, val resp: CompletableJob) : Action() {
            override fun invoke(device: Device, commandPool: VkCommandPool) {
                device.freeCommandBuffers(commandPool, cb)
                resp.complete()
            }
        }

        class Reset(val resp: CompletableJob) : Action() {
            override fun invoke(device: Device, commandPool: VkCommandPool) {
                device.resetCommandPool(commandPool)
                resp.complete()
            }
        }
    }


    companion object {

        val logger = KotlinLogging.logger { }

    }

    val commandPool: VkCommandPool = device.device.createCommandPool(
        CommandPoolCreateInfo(queueFamilyIndex, flags)
    )


    val wait = Channel<Pair<Job, CompletableJob>>(Channel.UNLIMITED)
    val wait_reset = Channel<Triple<Job, CompletableJob, CompletableJob>>(Channel.UNLIMITED)

    suspend fun wait(toWait: Job): Job {
        val resp = Job()
        wait.send(toWait to resp)
        return resp
    }
    suspend fun wait_reset(toWait: Job): Pair<CompletableJob, CompletableJob> {
        val resp = Job()
        val reset = Job()
        wait_reset.send(Triple(toWait, resp, reset))
        return resp to reset
    }
    suspend fun allocate(): CompletableDeferred<CommandBuffer> {
        val resp = CompletableDeferred<CommandBuffer>()
        actor.send(Action.AllocateCommandBuffer(resp = resp))
        return resp
    }
    suspend fun allocate(size: Int): CompletableDeferred<Array<CommandBuffer>> {
        val resp = CompletableDeferred<Array<CommandBuffer>>()
        actor.send(Action.AllocateCommandBuffers(size = size, resp = resp))
        return resp
    }

    suspend fun free(cb: CommandBuffer): Job {
        val resp = Job()
        actor.send(Action.FreeCommandBuffer(cb, resp))
        return resp
    }
    suspend fun reset(): Job {
        val resp = Job()
        actor.send(Action.Reset(resp))
        return resp
    }



    val actor = device.scope.actor<Action> {
        logger.info {
            "commandpool actor '$queueFamilyIndex.$flags' start"
        }

        while (isActive) {
            select<Unit> {
                wait.onReceive { (toWait, resp) ->
                    resp.complete()
                    toWait.join()
                }
                wait_reset.onReceive { (toWait, resp, reseted) ->
                    resp.complete()
                    toWait.join()
                    device.device.resetCommandPool(commandPool)
                    reseted.complete()
                }

                channel.onReceive {
                    it(device.device, commandPool)
                }
            }
        }

// never reached
//        logger.info {
//            "commandpool actor '$queueFamilyIndex.$flags' finish"
//        }

//        destroy()
    }



    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(device::destroy, this::destroy)
    }

    fun destroy() {
        device.device.destroy(commandPool)
    }






}