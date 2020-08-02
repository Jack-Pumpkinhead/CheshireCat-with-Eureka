package physics

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
    val rPow = disp.length2().toDouble().pow(power / 2).toFloat()   //r^pow
    disp.timesAssign(G * m * m_ / rPow)
    return disp
}

/*//F = G m m_/r^pow
//a_ impulse on a  (pull)
fun localGravity(
    p: Vec3,
    m: Float,
    p_: Vec3,
    m_: Float,
    power: Double = 2.0,
    G: Float = 1F,
    B: Float,
    dt: Float
): Vec3 {

    val disp = p_ - p
    val dist = disp.length()
    val maxF = m * B / (dt * dt)

    if (disp.allEqual(0F)) return Vec3()
    val length2 = disp.length2()
    val rPow = length2.toDouble().pow(power / 2).toFloat()   //r^pow
    disp.timesAssign(G * m * m_ / rPow)
    return disp
}*/


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
    val xPow = disp.length2().toDouble().pow(power / 2).toFloat()  //r^power
    disp.timesAssign(k * xPow / disp.length())
    return disp
}

//|F| = k * |r-d|^pow
//a_ impulse on a  (pull)
fun hooke(
    p: Vec3,
    p_: Vec3,
    d: Float,
    power: Double = 1.0,
    k: Float = 1F
): Vec3 {
    val disp = p_ - p

    val length = disp.length()
    return when {
        length == d -> Vec3()
        length < d -> {
            val xPow = (d - length).toDouble().pow(power).toFloat()  //|r-d|^power
            disp.timesAssign(-k * xPow / disp.length())
            disp
        }
        else -> {
            val xPow = (length - d).toDouble().pow(power).toFloat()  //|r-d|^power
            disp.timesAssign(k * xPow / disp.length())
            disp
        }
    }
}

//|F| = f(r)
//a_ impulse on a  (pull)
fun force(
    p: Vec3,
    p_: Vec3,
    f: (Float) -> Float
): Vec3 {
    val disp = p_ - p
    if(disp.allEqual(0F)) return Vec3()
    val r = disp.length()
    disp.timesAssign(f(r) / r)
    return disp
}

fun angleForce(
    p: Vec3,
    a: Vec3,
    b: Vec3,
    c: Float
): Vec3 {
    val disp = a + b   //disp = a + b - 2p
    disp.minusAssign(p.x * 2, p.y * 2, p.z * 2)
    return disp
}

fun snapToPlane(
    p: Vec3,
    center: Vec3,
    normal: Vec3,
    k: Float = 1F
): Vec3 {
    val disp = center - p
    val dot = disp.dot(normal)
    if (dot == 0F) return Vec3()
    val times = normal.times(dot / normal.length2())
    if (disp.dot(times) > 0) {
        times.timesAssign(k)
    } else {
        times.timesAssign(-k)
    }
    return times
}

val drag = DragForce({ 1F }, { Vec3() }, { 10F })

val dragWeak = DragForce({ 1F }, { Vec3() }, { 1F })


