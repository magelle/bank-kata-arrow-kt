package magelle.arrowkt.bankkata

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import io.kotlintest.shouldBe
import magelle.arrowkt.bankkata.account.*
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.time.LocalDate

@Suppress("unused")
object AccountFeature : Spek({
    Feature("Bank Account Management") {
        lateinit var result: Either<String, Account>

        Scenario("I should be able to get the statement") {
            Given("An account") {
                result = Account().right()
            }

            And("I made a deposit of 1000") {
                result = result.flatMap { deposit(it, 1000.amount(), LocalDate.of(2012, 1, 10)) }
            }

            And("I made a deposit of 2000") {
                result = result.flatMap { deposit(it, 2000.amount(), LocalDate.of(2012, 1, 13)) }
            }

            And("I did withdraw 500") {
                result = result.flatMap { withdraw(it, 500.amount(), LocalDate.of(2012, 1, 14)) }
            }

            Then("the statement should be ") {
                result.map { statement(it) } shouldBe listOf(
                    Movement(
                        date = "14/01/2012",
                        credit = "",
                        debit = "500",
                        balance = "2500"
                    ),
                    Movement(
                        date = "13/01/2012",
                        credit = "2000",
                        debit = "",
                        balance = "3000"
                    ),
                    Movement(
                        date = "10/01/2012",
                        credit = "1000",
                        debit = "",
                        balance = "1000"
                    )
                ).right()
            }
        }
    }

    Feature("Maximum withdrawal is balance, can't go under 0") {
        lateinit var result: Either<String, Account>

        Scenario("I should not be able to withdraw more than the balance") {
            Given("An account") {
                result = Account().right()
            }

            And("I made a deposit of 1000") {
                result = result.flatMap { deposit(it, 1000.amount(), LocalDate.of(2012, 1, 10)) }
            }

            When("I withdraw 1001") {
                result = result.flatMap { withdraw(it, 1001.amount(), LocalDate.of(2012, 1, 10)) }
            }

            Then("I get an error") {
                result shouldBe "You can't withdraw more than the balance.".left()
            }
        }

        Scenario("I should be able to withdraw when the balance is enough") {
            Given("An account") {
                result = Account().right()
            }

            And("I made a deposit of 1000") {
                result = result.flatMap { deposit(it, 1000.amount(), LocalDate.of(2012, 1, 10)) }
            }

            When("I withdraw 1001") {
                result = result.flatMap { withdraw(it, 1000.amount(), LocalDate.of(2012, 1, 10)) }
            }

            Then("I get an error") {
                result.isRight() shouldBe true
            }
        }
    }
})
