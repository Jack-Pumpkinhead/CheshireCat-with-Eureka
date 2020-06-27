package vulkan.concurrent

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.selects.select
import vkk.entities.*
import vkk.vk10.*
import vkk.vk10.structs.DescriptorSetAllocateInfo
import vulkan.OzDevice

/**
 * Created by CowardlyLion on 2020/5/5 16:04
 */
class OzDescriptorPool(val device: OzDevice,val  descriptorPool: VkDescriptorPool) {


    sealed class Action {

        class AllocateDS(
            val setLayout: VkDescriptorSetLayout,
            val resp: CompletableDeferred<VkDescriptorSet>
        ) : Action()
        class AllocateDSs(
            val setLayouts: VkDescriptorSetLayout_Array,
            val resp: CompletableDeferred<VkDescriptorSet_Array>
        ) : Action()


        class FreeDS(val descriptorSet: VkDescriptorSet, val resp: CompletableJob) : Action()
        class FreeDSs(val descriptorSets: VkDescriptorSet_Array, val resp: CompletableJob) : Action()
        class Reset(val resp: CompletableJob) : Action()

        class WaitComplete(val resp: CompletableJob) : Action()

    }

    suspend fun allocate(setLayouts: VkDescriptorSetLayout_Array): CompletableDeferred<VkDescriptorSet_Array> {
        val resp = CompletableDeferred<VkDescriptorSet_Array>()
        actor.send(Action.AllocateDSs(setLayouts, resp))
        return resp
    }
    suspend fun allocate(setLayouts: VkDescriptorSetLayout): CompletableDeferred<VkDescriptorSet> {
        val resp = CompletableDeferred<VkDescriptorSet>()
        actor.send(Action.AllocateDS(setLayouts, resp))
        return resp
    }
    fun allocate_im(setLayouts: VkDescriptorSetLayout): VkDescriptorSet {
        val resp = CompletableDeferred<VkDescriptorSet>()
        return runBlocking {
            actor.send(Action.AllocateDS(setLayouts, resp))
            return@runBlocking resp.await()
        }
    }


    suspend fun free(sets: VkDescriptorSet_Array): Job {
        val resp = Job()
        actor.send(Action.FreeDSs(sets, resp))
        return resp
    }
    suspend fun free(set: VkDescriptorSet): Job {
        val resp = Job()
        actor.send(Action.FreeDS(set, resp))
        return resp
    }

    suspend fun reset(): Job {
        val resp = Job()
        actor.send(Action.Reset(resp))
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
    suspend fun wait_reset(toWait: Job) = wait_reset.wait_reset(toWait)
    suspend fun wait_reset_im(toWait: Job) = wait_reset.wait_reset_im(toWait)
    suspend fun reset_im(): CompletableJob {
        val job = Job()
        job.complete()
        return wait_reset.wait_reset_im(job)
    }


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
                            is Action.AllocateDSs -> msg.resp.cancel()
                            is Action.FreeDSs -> msg.resp.cancel()
                            is Action.Reset -> msg.resp.cancel()
                        }
                        msg = channel.poll()
                    }
                    device.device.resetDescriptorPool(descriptorPool, 0)
                    reseted.complete()
                }

                channel.onReceive {msg ->
                    when (msg) {
                        is Action.AllocateDSs -> msg.resp.complete(
                            device.device.allocateDescriptorSets(
                                DescriptorSetAllocateInfo(
                                    descriptorPool = descriptorPool,
                                    setLayouts = msg.setLayouts
                                )
                            )
                        )
                        is Action.AllocateDS -> msg.resp.complete(
                            device.device.allocateDescriptorSet(
                                DescriptorSetAllocateInfo(
                                    descriptorPool = descriptorPool,
                                    setLayouts = VkDescriptorSetLayout_Array(1) { msg.setLayout }
                                )
                            )
                        )
                        is Action.FreeDSs -> {
                            device.device.freeDescriptorSets(
                                descriptorPool, msg.descriptorSets
                            )
                            msg.resp.complete()
                        }
                        is Action.FreeDS -> {
                            device.device.freeDescriptorSets(
                                descriptorPool, msg.descriptorSet
                            )
                            msg.resp.complete()
                        }
                        is Action.Reset -> {
                            device.device.resetDescriptorPool(descriptorPool, 0)
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
        device.device.destroy(descriptorPool)
    }

}