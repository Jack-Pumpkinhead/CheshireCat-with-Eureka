package physics

import assimp.isBlack
import glm_.vec3.Vec3
import kotlin.math.pow

/**
 * Created by CowardlyLion on 2020/7/6 21:35
 */

//F = G m m_/r^pow
//a_ impulse on a  (pull)
fun gravity(
    p: Vec3,
    m: Float,
    p_: Vec3,
    m_: Float,
    power: Double = 2.0,
    G: Float = 1F
): Vec3 {
    val disp = p_ - p
    if(disp.allEqual(0F)) return Vec3()
    val R = disp.length2().toDouble().pow(power / 2).toFloat()
    disp.timesAssign(G * m * m_ / R)
    return disp
}

//|F| = k * r^pow
//a_ impulse on a  (pull)
fun hooke(
    p: Vec3,
    p_: Vec3,
    power: Double = 2.0,
    k: Float = 1F
): Vec3 {
    val disp = p_ - p
    if(disp.allEqual(0F)) return Vec3()
    val R = disp.length2().toDouble().pow(power / 2).toFloat()  //r^power
    disp.timesAssign(k * R / disp.length())
    return disp
}

//|F| = k(r)
//a_ impulse on a  (pull)
fun force(
    p: Vec3,
    p_: Vec3,
    k: (Float) -> Float
): Vec3 {
    val disp = p_ - p
    if(disp.allEqual(0F)) return Vec3()
    val r = disp.length()
    disp.timesAssign(k(r) / r)
    return disp
}





val drag = DragForce({ 1F }, { Vec3() }, { 10F })


