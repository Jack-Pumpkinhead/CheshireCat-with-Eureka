package math

import glm_.vec3.Vec3
import kotlin.random.Random

/**
 * Created by CowardlyLion on 2020/7/6 21:04
 */

fun randomColor(): Vec3 = Vec3(Random.nextFloat(), Random.nextFloat(), Random.nextFloat())
fun random(radius: Float): Float = Random.nextInt().toFloat()*radius/Int.MAX_VALUE
fun randomVec3(radius: Float) = Vec3(random(radius), random(radius), random(radius))
