package math.category

import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

/**
 * Created by CowardlyLion on 2020/7/4 15:58
 */
class CatsTest:StringSpec(body = {
    "all build-in cats is cat"{
        Cats().list.all { it.isCategory() } shouldBe true
    }



}) {

}