package math.category

import game.main.Univ

/**
 * Created by CowardlyLion on 2020/7/4 15:36
 */
class Cats {
    val trivial = FinCategory(
        objs = 1, arrows = 0, comp = emptyList(), s = intArrayOf(), t = intArrayOf()
    )
    val idempotent = FinCategory(
        objs = 1, arrows = 1, comp = listOf(intArrayOf(0)), s = intArrayOf(0), t = intArrayOf(0)
    )
    val involution = FinCategory(
        objs = 1, arrows = 1, comp = listOf(intArrayOf(-1)), s = intArrayOf(0), t = intArrayOf(0)
    )
    val bundle = FinCategory(
        objs = 2, arrows = 1, comp = emptyList(), s = intArrayOf(0), t = intArrayOf(1)
    )
    val quiver = FinCategory(
        objs = 2, arrows = 2, comp = emptyList(), s = intArrayOf(0, 0), t = intArrayOf(1, 1)
    )

    val list = listOf(
        trivial,
        idempotent,
        involution,
        bundle,
        quiver
    )
    init {
//        Univ.logger.info {
//            "cat: ${list.all { it.isCategory() }}"
//        }
    }







}