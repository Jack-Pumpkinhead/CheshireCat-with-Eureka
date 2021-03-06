package game.entity

import glm_.vec3.Vec3
import math.vector.addVec
import math.vector.addVecRel
import physics.NewtonPoint
import physics.particles.ParticleComponent

/**
 * Created by CowardlyLion on 2020/8/11 22:46
 */
class Lines(
    val points: MutableList<NewtonPoint> = mutableListOf(),
    val colors: MutableList<Vec3> = mutableListOf(),
    val lines: MutableList<Int> = mutableListOf(),
    val fixes: MutableList<Boolean> = mutableListOf(),
    val components: MutableList<ParticleComponent> = mutableListOf()
) {

    fun add(point: NewtonPoint, color: Vec3, fix: Boolean = false): Int {
        points += point
        colors += color
        fixes += fix
        return points.size - 1
    }

    fun addLine(from: Int, to: Int) {
        lines += from
        lines += to
    }

    fun vertexData(data: MutableList<Float>) {
        for (i in points.indices) {
            data.addVec(points[i].p)
            data.addVec(colors[i])
        }
    }
    fun vertexData(data: MutableList<Float>, rel: Vec3) {
        for (i in points.indices) {
            data.addVecRel(points[i].p, rel)
            data.addVec(colors[i])
        }
    }
    fun indexData(data: MutableList<Int>, bias: Int){
        lines.forEach {
            data += it + bias
        }
    }





    fun update() {

        points.forEach { it.f.put(0F, 0F, 0F) }

        components.forEach {
            it.update()
        }

        points.filterIndexed { i, _ -> !fixes[i] }
            .forEach { it.update() }
    }

}