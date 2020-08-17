package land.Oz.Quadling.quad

import glm_.vec3.Vec3
import land.Oz.Quadling.cat.squareLessExact
import math.vector.addVec
import math.vector.addVecRel
import physics.NewtonPoint

/**
 * Created by CowardlyLion on 2020/8/4 12:56
 */
class Quad(
    val center: Vec3,
    val right: Vec3,
    val face: Vec3,
    val down: Vec3 = right.cross(face)
) {

}


