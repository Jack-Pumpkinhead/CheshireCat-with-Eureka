package land.Oz.Quadling.cat

import math.randomVec3
import physics.NewtonPoint

/**
 * Created by CowardlyLion on 2020/8/8 23:19
 */

fun arrOf(s: CatPoint3, t: CatPoint3): CatArr3 {
    val pa = NewtonPoint(p = randomVec3(s.center.p, 0.1F))
    val pb = NewtonPoint(p = randomVec3(t.center.p, 0.1F))
    val pm = NewtonPoint(p = (pa.p + pb.p)/2)
    return CatArr3(
        source = s,
        target = t,
        points = mutableListOf(pa, pm, pm, pb),
        colors = mutableListOf(green, green, blue, blue),
        lines = mutableListOf(0, 1, 2, 3)
    )
}