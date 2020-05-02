package vulkan

import com.google.common.collect.TreeMultiset
import game.main.OzConstants.Extensions
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.sync.Mutex
import mu.KotlinLogging
import vkk.VkFenceCreate
import vkk.VkFenceCreateFlags
import vkk.entities.VkFence
import vkk.entities.VkSemaphore
import vkk.identifiers.Queue
import vkk.vk10.*
import vkk.vk10.structs.DeviceCreateInfo
import vkk.vk10.structs.DeviceQueueCreateInfo
import vkk.vk10.structs.FenceCreateInfo
import vulkan.concurrent.OzQueue
import vulkan.util.SurfaceSwapchainSupport

class OzDevice(
    val ozVulkan: OzVulkan,
    val ozPhysicalDevice: OzPhysicalDevice,
    val surfaceSupport: SurfaceSwapchainSupport
) {

    val logger = KotlinLogging.logger { }

    val graphicI = 0
    val presentI: Int
    val transferI: Int
    val queueCIs: List<DeviceQueueCreateInfo>
    init {

        val set = TreeMultiset.create<Int>()
        set.add(surfaceSupport.queuefamily_graphic)

        presentI =  //temporary solution
            set.count(surfaceSupport.queuefamily_present) % ozPhysicalDevice.queueFamilyProperties[surfaceSupport.queuefamily_present].queueCount
        set.add(surfaceSupport.queuefamily_present)

        transferI = set.count(surfaceSupport.queuefamily_transfer) % ozPhysicalDevice.queueFamilyProperties[surfaceSupport.queuefamily_transfer].queueCount
        set.add(surfaceSupport.queuefamily_graphic)

        val q = mutableListOf<DeviceQueueCreateInfo>()
        set.forEachEntry { index, count ->
            q += DeviceQueueCreateInfo(
                queueFamilyIndex = index,
                queuePriorities = FloatArray(count) { 0.5f }) //Specify the number of queues in this queueFamilyIndex to create. will work even slightly larger than queueFamProp[0].queueCount
        }
        queueCIs = q.toList()
    }

    val deviceCI = DeviceCreateInfo(
        queueCreateInfos = queueCIs,
        enabledExtensionNames = Extensions,
        enabledFeatures = ozPhysicalDevice.features
    )
    val device = ozPhysicalDevice.pd.createDevice(deviceCI)

    val graphicsQueue : Queue = device.getQueue(surfaceSupport.queuefamily_graphic,queueIndex = graphicI)
    val presentQueue: Queue = device.getQueue(surfaceSupport.queuefamily_present,queueIndex = presentI)
    val transferQueue: Queue = device.getQueue(surfaceSupport.queuefamily_transfer, queueIndex = transferI)

//    val mts = mapOf<Pair<Int, Int>, Mutex>()
//    val fences = mapOf<Pair<Int, Int>, VkFence>()

    //temp solution, may support more queue
    //kotlin coroutine: select on send
//    fun graphicsMT() = mts.getOrDefault(surfaceSupport.queuefamily_graphic to graphicI, Mutex())
//    fun presentMT() = mts.getOrDefault(surfaceSupport.queuefamily_present to presentI, Mutex())
//    fun transferMT() = mts.getOrDefault(surfaceSupport.queuefamily_transfer to transferI, Mutex())

    fun semaphore(): VkSemaphore = device.createSemaphore()
    fun signaledFence(flag: VkFenceCreateFlags = VkFenceCreate.SIGNALED_BIT.i) = device.createFence(FenceCreateInfo(flag))


//    fun graphicsFence() = fences.getOrDefault(surfaceSupport.queuefamily_graphic to graphicI, signaledFence())
//    fun presentFence()  = fences.getOrDefault(surfaceSupport.queuefamily_present to presentI, signaledFence())
//    fun transferFence() = fences.getOrDefault(surfaceSupport.queuefamily_transfer to transferI, signaledFence())

//    val queueActions = Channel<(Queue) -> Unit>()


    init {
//        graphicsQueue
//        device.withFence {
//
//        }
//        device.getFenceStatus(
//        graphicsFence())
//        queueActions.re
    }

    val scope = CoroutineScope(Dispatchers.Default)
//    val scope = CoroutineScope(newSingleThreadContext("device"))

    //make sure queues are different
    val graphicQ  =
        OzQueue(ozVulkan, this, surfaceSupport.queuefamily_graphic, graphicI)
    val presentQ  =
        OzQueue(ozVulkan, this, surfaceSupport.queuefamily_present, presentI)
    val transferQ =
        OzQueue(ozVulkan, this, surfaceSupport.queuefamily_transfer, transferI)

    suspend fun onRecreateRenderpass(job: CompletableJob):List<Job> {
        transferQ.wait(job)
        return listOf(
            graphicQ.wait_clear(job),
            presentQ.wait_clear(job)
        )
    }




    init {
        ozVulkan.cleanups.addNode(this::destroy)
        ozVulkan.cleanups.putEdge(ozPhysicalDevice.ozInstance::destroy, this::destroy)
    }

    fun destroy() {

        device.waitIdle()
        device.destroy()
    }


}