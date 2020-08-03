package land.Oz.Quadling.cat

import glm_.vec3.Vec3
import math.randomVec3
import physics.NewtonPoint

/**
 * Created by CowardlyLion on 2020/7/31 23:16
 */

val green = Vec3.fromColor(30, 120, 70)
val blue = Vec3.fromColor(30, 10, 130)
val blue2 = Vec3.fromColor(10, 30, 100)
val red = Vec3.fromColor(100, 50, 60)
val purple = Vec3.fromColor(174, 56, 242)

fun tetrahedron(center: Vec3, radius: Float = 1F, lineDist: Float = 0.5F) = CatPoint(
    center = NewtonPoint(p = center),
    points = mutableListOf(
        NewtonPoint(p = randomVec3(center, radius)),
        NewtonPoint(p = randomVec3(center, radius)),
        NewtonPoint(p = randomVec3(center, radius)),
        NewtonPoint(p = randomVec3(center, radius))
    ),
    colors = mutableListOf(green, green, green, green),
    lines = mutableListOf(0, 1, 0, 2, 0, 3, 1, 2, 1, 3, 2, 3),
    lineDist = lineDist
)
/*
fun empty(center: Vec3, lineAttractForce: Float = 0.5F) = CatPoint(
    center = NewtonPoint(p = center),
    points = mutableListOf(),
    colors = mutableListOf(),
    lines = mutableListOf(),
    lineDist = lineDist
)
*/


fun smallTetrahedron(center: Vec3) = tetrahedron(center, 0.1F, 0.05F)


fun cube(center: Vec3, lineDist: Float = 0.5F) = CatPoint(
    center = NewtonPoint(p = center),
    points = mutableListOf(
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F))
    ),
    colors = mutableListOf(green, green, green, green, green, green, green, green),
    lines = mutableListOf(
        0, 1, 1, 2, 2, 3, 3, 0,
        4, 5, 5, 6, 6, 7, 7, 4,
        0, 4, 1, 5, 2, 6, 3, 7
    ),
    lineDist = lineDist
)

fun cubeExact(center: Vec3, lineDist: Float = 2F) = CatPoint(
    center = NewtonPoint(p = center),
    points = mutableListOf(
        NewtonPoint(p = center + Vec3(-1F, -1F, -1F)),
        NewtonPoint(p = center + Vec3(-1F, -1F, +1F)),
        NewtonPoint(p = center + Vec3(-1F, +1F, +1F)),
        NewtonPoint(p = center + Vec3(-1F, +1F, -1F)),
        NewtonPoint(p = center + Vec3(+1F, -1F, -1F)),
        NewtonPoint(p = center + Vec3(+1F, -1F, +1F)),
        NewtonPoint(p = center + Vec3(+1F, +1F, +1F)),
        NewtonPoint(p = center + Vec3(+1F, +1F, -1F))
    ),
    colors = mutableListOf(green, green, green, green, green, green, green, green),
    lines = mutableListOf(
        0, 1, 1, 2, 2, 3, 3, 0,
        4, 5, 5, 6, 6, 7, 7, 4,
        0, 4, 1, 5, 2, 6, 3, 7
    ),
    lineDist = lineDist
)

fun squareLessExact(center: Vec3, lineDist: Float = 2F) = CatPoint2(
    center = NewtonPoint(p = center),
    points = mutableListOf(
        NewtonPoint(p = center + Vec3(-1F, -1F, 0F)),
        NewtonPoint(p = center + Vec3(-1F, +1F, 0F)),
        NewtonPoint(p = center + Vec3(+1F, +1F, 0F)),
        NewtonPoint(p = center + Vec3(+1F, -1F, 0F) + randomVec3(0.01F))
    ),
    colors = mutableListOf(green, green, green, green),
    actives = mutableListOf(true, true, true, true),
    lines = mutableListOf(
        0, 1, 1, 2, 2, 3, 3, 0
    ),
    lineDist = lineDist
)

