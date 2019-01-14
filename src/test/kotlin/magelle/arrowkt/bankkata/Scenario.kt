package magelle.arrowkt.bankkata

import io.kotlintest.shouldBe
import magelle.arrowkt.bankkata.domain.Account
import magelle.arrowkt.bankkata.domain.deposit
import magelle.arrowkt.bankkata.domain.withdraw
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.time.LocalDate

@Suppress("unused")
object SetFeature : Spek({
    Feature("Bank Account Management") {
        lateinit var account: Account


        Scenario("I should be able to get the statement") {
            Given("An account") {
                account = Account()
            }

            And("I made a deposit of 1000") {
                account = deposit(account, 1000, LocalDate.of(2012, 1, 10))
            }

            And("I made a deposit of 2000") {
                account = deposit(account, 2000, LocalDate.of(2012, 1, 13))
            }

            And("I did withdraw 500") {
                account = withdraw(account, 500, LocalDate.of(2012, 1, 14))
            }

            Then("the statement should be ") {
                print(account)
                statement(account) shouldBe listOf(
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
                )
            }
        }

    }
})
