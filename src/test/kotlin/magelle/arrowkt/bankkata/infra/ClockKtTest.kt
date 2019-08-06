package magelle.arrowkt.bankkata.infra

import io.kotlintest.should
import io.kotlintest.shouldNotBe
import io.kotlintest.specs.StringSpec
import jdk.internal.dynalink.support.Guards.isNotNull

@Suppress("unused")
class ClockKtTest : StringSpec({
    "I should be able know the actual date" {
        now() shouldNotBe null
    }
})