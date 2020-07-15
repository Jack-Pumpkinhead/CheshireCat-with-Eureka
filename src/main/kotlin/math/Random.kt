package math

import glm_.vec3.Vec3
import kotlin.random.Random
import kotlin.random.nextUInt

/**
 * Created by CowardlyLion on 2020/7/6 21:04
 */

fun randomColor(): Vec3 = Vec3(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
fun randomColor(bias: Float): Vec3 = Vec3(random(bias, 1F), random(bias, 1F), random(bias, 1F))
fun random(radius: Float): Float = Random.nextInt().toFloat()*radius/Int.MAX_VALUE  // maybe (-radius,radius]

//@ExperimentalUnsignedTypes
fun random(from: Float = 0F, to: Float = 1F): Float =
    from + (Random.nextUInt().toFloat() * (to - from) / UInt.MAX_VALUE.toFloat())  // maybe [from, to]

fun randomVec3(radius: Float) = Vec3(random(radius), random(radius), random(radius))
fun randomVec3(center: Vec3, radius: Float) = center.plus(random(radius), random(radius), random(radius))
