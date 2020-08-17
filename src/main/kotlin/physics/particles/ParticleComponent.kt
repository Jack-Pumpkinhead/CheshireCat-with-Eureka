package physics.particles

import physics.NewtonPoint

/**
 * Created by CowardlyLion on 2020/8/11 19:58
 */
abstract class ParticleComponent(
    val points: List<NewtonPoint>
) {

    abstract fun update()

}

abstract class ParticleComponentS(
    val points: List<NewtonPoint>
) {

    abstract suspend fun update()

}

abstract class ParticleComponentT(
    val points: List<NewtonPoint>
) {

    abstract fun update(tick: Long)

}

abstract class ParticleComponentTS(
    val points: List<NewtonPoint>
) {

    abstract suspend fun update(tick: Long)

}

abstract class ParticleComponentTT(
    val points: List<NewtonPoint>
) {

    abstract fun update(tick: Long, timemillis: Long)

}

abstract class ParticleComponentTTS(
    val points: List<NewtonPoint>
) {

    abstract suspend fun update(tick: Long, timemillis: Long)

}


