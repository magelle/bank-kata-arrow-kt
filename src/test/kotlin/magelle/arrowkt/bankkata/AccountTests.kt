package magelle.arrowkt.bankkata

import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import magelle.arrowkt.bankkata.account.Movement
import java.time.LocalDate

class AccountTests : StringSpec({
    "should be able to make deposit, withdrawal and get statement" {
        createAccount()
            .flatMap {
                nowIs(LocalDate.of(2012, 1, 10))
                makeDeposit(it, 1000)
            }
            .flatMap {
                nowIs(LocalDate.of(2012, 1, 13))
                makeDeposit(it, 2000)
            }
            .flatMap {
                nowIs(LocalDate.of(2012, 1, 14))
                makeWithdrawal(it, 500)
            }
            .flatMap { printStatement(it) } shouldBe listOf(
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

    "should not allow to withdraw more than you have" {
        createAccount()
            .flatMap {
                makeDeposit(it, 1000)
            }
            .flatMap {
                makeWithdrawal(it, 1001)
            } shouldBe "You can't withdraw more than the balance.".left()
    }

    "Should allow to withdraw all the money" {
        createAccount()
            .flatMap {
                makeDeposit(it, 1000)
            }
            .flatMap {
                makeWithdrawal(it, 1000)
            }.isRight() shouldBe true
    }
})