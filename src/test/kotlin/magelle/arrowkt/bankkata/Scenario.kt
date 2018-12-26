package magelle.arrowkt.bankkata

import io.kotlintest.matchers.collections.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature

@Suppress("unused")
object SetFeature: Spek({
    Feature("Set") {
        val set by memoized { mutableSetOf<String>() }

        Scenario("adding items") {
            When("adding foo") {
                set.add("foo")
            }

            Then("it should have a size of 1") {
                set.size shouldBe 1
            }

            Then("it should contain foo") {
                set shouldContain "foo"
            }
        }

        Scenario("empty") {
            Then("should have a size of 0") {
                set.size shouldBe 0
            }

            Then("should throw when first is invoked") {
                shouldThrow<NoSuchElementException> {
                    set.first()
                }
            }
        }

        Scenario("getting the first item") {
            val item = "foo"
            Given("a non-empty set")  {
                set.add(item)
            }

            lateinit var result: String

            When("getting the first item") {
                result = set.first()
            }

            Then("it should return the first item") {
                result shouldBe item
            }
        }
    }
})
