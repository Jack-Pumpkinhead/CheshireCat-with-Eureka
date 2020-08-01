package land.Oz.Quadling.texting

import game.main.Univ
import glm_.vec3.Vec3
import glm_.vec3.swizzle.xyz
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import math.matrix.InArrModel
import math.minIndex
import math.randomVec3
import math.vector.distance2
import physics.*
import vulkan.command.BindMVPTexture
import vulkan.command.bindSet
import vulkan.pipelines.descriptor.fetchModel
import kotlin.math.atan
import kotlin.random.Random

/**
 * Created by CowardlyLion on 2020/7/25 21:42
 */
class TextAlignment(val univ: Univ, val cursor: NewtonPoint) {


    val text = mutableListOf<NewtonPoint>()
    val textPool = mutableListOf<NewtonPoint>()

    val left = NewtonPoint(cursor.p.xyz)

    val textModel = mutableListOf<InArrModel>()
    val textModelB = mutableListOf<BindMVPTexture>()
    val textPoolModel = mutableListOf<InArrModel>()
    val textPoolModelB = mutableListOf<BindMVPTexture>()

    suspend fun init() {
        mutex.withLock {

            repeat(200) {
                val point = NewtonPoint(p = cursor.p + randomVec3(5F))
                textPool += point
                val model = univ.fetchModel()
                model.pos = point.p
                model.update()
                textPoolModel += model
                textPoolModelB += univ.bindSet(model,univ.loader.textureSets.Icosphere_blue)
            }
        }
    }

    suspend fun texting() {
        mutex.withLock {
            if (textPool.isNotEmpty()) {
                cursorMoveRight()
                val upperIndex = textPool.map { it.p.y }.minIndex()
                move(upperIndex)

            }
        }
    }
    fun move(index: Int) {

        val t = textPool.removeAt(index)
        val tM = textPoolModel.removeAt(index)
        val tMB = textPoolModelB.removeAt(index)
        text += t
        textModel += tM
        textModelB += tMB
        tMB.texture = univ.loader.textureSets.Icosphere_green
    }
    val rightPos = Vec3(1F, 0F, 0F)

    val cursorDst = cursor.p.xyz
    fun cursorMoveRight() {
        cursorDst.plusAssign(rightPos)
    }

    var pivotForce: Float = 10F
    var snapTime = 0.5F
    suspend fun update() {
        mutex.withLock {
            val f = force(cursor.p, cursorDst) { r -> pivotForce * atan(r) }
            cursor.f.xyz = f
//            } else {
//                val dist = sqrt(dist2)
//                val v = 2 * dist / snapTime
//                letter.v.xyz = v
//
//            }
            cursor.f.plusAssign(drag.get(cursor.p, cursor.v))
            cursor.update()

            val dst = left.p.xyz
            for (letter in text) {
                val dist2 = distance2(letter.p, dst)
//            if (dist2 < 0.01F) {
                val f = force(letter.p, dst) { r -> pivotForce * atan(r) }
                letter.f.xyz = f
//            } else {
//                val dist = sqrt(dist2)
//                val v = 2 * dist / snapTime
//                letter.v.xyz = v
//
//            }
                letter.f.plusAssign(drag.get(letter.p, letter.v))
                letter.update()
                dst.plusAssign(rightPos)
            }

            for (letter in textPool) {
                letter.f.put(0, 0, 0)
            }


            for (i in textPool.indices) { //互相排斥
                for (j in 0 until i) {
                    val a = textPool[i]
                    val b = textPool[j]
                    val f = gravity(a.p, a.m, b.p, b.m, 2.0, -0.05F)
                    a.f.plusAssign(f)
                    b.f.plusAssign(-f)
                }
            }
            for (letter in textPool) {
                letter.f.plusAssign(0, pivotForce/30F, 0)
            }
            for (i in textPool.indices) { //互相拉近
                for (j in 0 until i) {
                    val a = textPool[i]
                    val b = textPool[j]
                    val f = force(a.p, b.p) { r -> pivotForce/3000F * atan(r) }
                    a.f.plusAssign(f)
                    b.f.plusAssign(-f)
                }
            }
            for (i in textPool.indices) { //流体阻力
                val a = textPool[i]
//                a.f.plusAssign(drag.get(a.p, a.v))
                val f = force(a.p, cursorDst) { r -> pivotForce*0.01F * atan(r) }
//                val f = hooke(a.p, cursorDst)
                a.f.plusAssign(f)

                if (a.p.y > left.p.y + 20) {
                    a.f.y = 0F
                    a.v.y = 0F
                }
            }
            if (textPool.size > 50) {
                val i = Random.nextInt(textPool.size)
                textPool[i].v.plusAssign(0, -1, 0)
            }


            textPool.forEach { it.update() }


            textModel.forEach {
                it.update()
            }
            textPoolModel.forEach {
                it.update()
            }
        }
    }

    val mutex = Mutex()
}