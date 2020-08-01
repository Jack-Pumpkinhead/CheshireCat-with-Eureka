package math.maybespring_deprecated

/**
 * Created by CowardlyLion on 2020/5/7 13:03
 */
class Extra {
/*
    val cache = MultimapBuilder.hashKeys().linkedListValues().build<String, Ex>()


    val scope = CoroutineScope(Dispatchers.Default)


    val subsequentUpdateMap = MultimapBuilder.hashKeys().arrayListValues().build<KClass<out Ex>, Ex>()
    val destroyDependencyMap = MultimapBuilder.hashKeys().arrayListValues().build<KClass<out Ex>, Ex>()


    val creationTask = mutableListOf<Ex>()
    val destroyTask = mutableListOf<Ex>()


    val versionRequestDependency = MultimapBuilder.hashKeys().arrayListValues().build<KClass<out Ex>, KClass<out Ex>>() //setup at class initialization
    val versionMap = mutableMapOf<KClass<out Ex>, Long>()
    val versionRequestMap = mutableMapOf<KClass<out Ex>, Long>()
    val versionRequestChannel = Channel<Pair<KClass<out Ex>, Long>>()
    val maskVersionRequest = scope.launch {
        val versionRequestTask = mutableListOf<Pair<KClass<out Ex>, Long>>()
        for ((clazz, vr) in versionRequestChannel) {
            val vr_ = versionRequestMap[clazz]
            if (vr_ != null && vr <= vr_) continue

            versionRequestMap[clazz] = vr
            versionRequestDependency[clazz].forEach {
                versionRequestTask += it to vr
            }

        }
    }


    val creation = scope.launch {

    }

*/







}
