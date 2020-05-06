package math.cat

import kotlinx.coroutines.Job
import mu.KotlinLogging

/**
 * Created by CowardlyLion on 2020/5/6 18:31
 */
class Cat {

    companion object {

        val logger = KotlinLogging.logger { }

    }

    init {
        var job:Any = Job()
        when (job) {
//            is (Cat,Cat)->Cat -> 3
            is Cat -> 3
        }

    }


}