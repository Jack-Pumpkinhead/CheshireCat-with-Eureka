package physics.graph

import glm_.vec3.Vec3
import math.randomColor
import physics.NewtonPoint

/**
 * Created by CowardlyLion on 2020/7/13 18:22
 */
class Line(
    val src: NewtonPoint,
    val tar: NewtonPoint,
    val srcColor: Vec3 = randomColor(0.1F),
    val tarColor: Vec3 = srcColor
) {



}