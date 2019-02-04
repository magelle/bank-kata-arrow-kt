package magelle.arrowkt.bankkata

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import arrow.effects.IO
import io.kotlintest.shouldBe
import magelle.arrowkt.bankkata.account.Movement
import magelle.arrowkt.bankkata.account.usecase.askForAccountCreation
import magelle.arrowkt.bankkata.account.usecase.askForDeposit
import magelle.arrowkt.bankkata.account.usecase.askForWithdrawal
import magelle.arrowkt.bankkata.account.usecase.printStatementQuery
import magelle.arrowkt.bankkata.infra.getAccount
import magelle.arrowkt.bankkata.infra.provideAccountId
import magelle.arrowkt.bankkata.infra.saveAccount
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.gherkin.Feature
import java.time.LocalDate

@Suppress("unused")
object AccountIT : Spek({
    Feature("Bank Account Management") {
        var accountId: Int = 0

        Scenario("I should be able to get the statement") {
            Given("An account") {
                accountId = createAccount()
            }

            And("I made a deposit of 1000") {
                nowIs(LocalDate.of(2012, 1, 10))
                makeDeposit(accountId, 1000)
            }

            And("I made a deposit of 2000") {
                nowIs(LocalDate.of(2012, 1, 13))
                makeDeposit(accountId, 2000)
            }

            And("I did withdraw 500") {
                nowIs(LocalDate.of(2012, 1, 14))
                makeWithdrawal(accountId, 500)
            }

            Then("the statement should be ") {
                printStatement(accountId) shouldBe listOf(
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
        var accountId: Int = 0
        lateinit var result: Either<String, Int>

        Scenario("I should not be able to withdraw more than the balance") {
            Given("An account") {
                accountId = createAccount()
            }

            And("I made a deposit of 1000") {
                result = makeDeposit(accountId, 1000)
            }

            When("I withdraw 1001") {
                result = makeWithdrawal(accountId, 1001)
            }

            Then("I get an error") {
                result shouldBe "You can't withdraw more than the balance.".left()
            }
        }

        Scenario("I should be able to withdraw when the balance is enough") {
            Given("An account") {
                accountId = createAccount()
            }

            And("I made a deposit of 1000") {
                result = makeDeposit(accountId, 1000)
            }

            When("I withdraw 1001") {
                result = makeWithdrawal(accountId, 1000)
            }

            Then("I get an error") {
                result.isRight() shouldBe true
            }
        }
    }

})

val createAccount = askForAccountCreation(provideAccountId, saveAccount)
val makeDeposit = askForDeposit(now, getAccount, saveAccount)
val makeWithdrawal = askForWithdrawal(now, getAccount, saveAccount)
val printStatement = printStatementQuery(getAccount)