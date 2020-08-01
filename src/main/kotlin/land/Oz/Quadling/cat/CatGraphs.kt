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

fun tetrahedron(center: Vec3, lineAttractForce: Float = 0.5F) = CatGraph(
    center = NewtonPoint(p = center),
    points = mutableListOf(
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F)),
        NewtonPoint(p = randomVec3(center, 1F))
    ),
    colors = mutableListOf(green, green, green, green),
    lines = mutableListOf(0, 1, 0, 2, 0, 3, 1, 2, 1, 3, 2, 3),
    lineAttractForce = lineAttractForce
)
fun smallTetrahedron(center: Vec3) = tetrahedron(center, 5F)

fun cube(center: Vec3) = CatGraph(
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
    )
)
