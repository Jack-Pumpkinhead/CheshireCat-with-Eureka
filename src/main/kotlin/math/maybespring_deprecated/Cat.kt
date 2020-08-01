package math.maybespring_deprecated

import com.google.common.collect.MultimapBuilder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.actor
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.reflect.KClass
import kotlin.reflect.cast

/**
 * Created by CowardlyLion on 2020/5/6 18:31
 */
@ExperimentalStdlibApi
class Cat {

    companion object {

        val logger = KotlinLogging.logger { }

        val coroutineScope = CoroutineScope(Dispatchers.Default)


        val actor = coroutineScope.actor<Action<Cat>> {
            val cache = mutableMapOf<String, MutableList<Cat>>()
            loop@ for (msg in channel) {
                when (msg) {
                    is Action.Named -> {
                        val (name,result) = msg
                        //call depend class's actor

                        //if containkey&&newest return current else create new one
                        val list = cache.getOrPut(name, ::mutableListOf)

                        if (list.isNotEmpty()) {
                            val current = list.last()
                            if (true) { //check if should be updated
                                result.complete(current)
                                continue@loop
                            }
                        }
                        val current = Cat()// create new one
                        list += current
                        result.complete(current)


                    }
                    is Action.Add -> TODO()
                    is Action.Destroy -> TODO()
                    is Action.Forget -> TODO()
                    is Action.DestroyAll -> TODO()
                }
            }
        }
    }
    val createTime = System.currentTimeMillis()

    init {
        var job:Any = Job()
        when (job) {
//            is (Cat,Cat)->Cat -> 3
            is Cat -> 3
        }

//        mutableMapOf<KClass<Any>,SendChannel>()
//        val kClass: KClass<Cat> = Cat::class
//        kClass.constructors.first().parameters.forEach {
//            it.type
//        }
//LinkedListMultimap
        val cache = MultimapBuilder.hashKeys().linkedListValues().build<String, Cat>()
        val list = LinkedList<Cat>()
        val ccc = ConcurrentLinkedDeque<Cat>()
        ccc.first
        list.pollFirst()
        val con = ConcurrentHashMap<String, Cat>()
//SharedImmutable
        val cast = Cat::class.cast(this)

        val dependency = ConcurrentHashMap<KClass<*>, ConcurrentHashMap<String, Any>>()
        val dependency2 = ConcurrentHashMap<Pair<KClass<*>, String>, Any>()
        val dependency3 = ConcurrentHashMap<Pair<KClass<*>, String>, Ex<*>>()

        dependency.put(Cat::class, ConcurrentHashMap())
        val cast1 = Cat::class.cast(dependency.get(Cat::class)?.get("aaaa"))
        val cast2 = Ex::class.cast(dependency.get(Cat::class)?.get("aaaa"))


    }

    sealed class Action<T>{
        data class Named<T>(val name: String, val result: CompletableDeferred<T>):Action<T>()
        data class Add<T>(val name: String, val obj: T, val result: CompletableDeferred<T>):Action<T>()
        data class Destroy<T>(val name: String, val obj: T, val result: CompletableDeferred<T>):Action<T>()
        data class Forget<T>(val name: String, val timestamp: Long, val result: CompletableDeferred<T>):Action<T>()
        data class DestroyAll<T>(val name: String, val result: CompletableDeferred<T>):Action<T>()



    }





}