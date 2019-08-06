package magelle.arrowkt.bankkata

import arrow.core.flatMap
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import magelle.arrowkt.bankkata.account.*
import java.time.LocalDate

@Suppress("unused")
class AccountTest : StringSpec({
    "I should be able to get the statement" {
        Account(AccountId(1)).right()
            .flatMap { deposit(it, 1000.amount(), LocalDate.of(2012, 1, 10)) }
            .flatMap { deposit(it, 2000.amount(), LocalDate.of(2012, 1, 13)) }
            .flatMap { withdraw(it, 500.amount(), LocalDate.of(2012, 1, 14)) }
            .map { statement(it) } shouldBe listOf(
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

    "I should not be able to withdraw more than the balance" {
        val result = Account(AccountId(1)).right()
            .flatMap { deposit(it, 1000.amount(), LocalDate.of(2012, 1, 10)) }
            .flatMap { withdraw(it, 1001.amount(), LocalDate.of(2012, 1, 10)) }
        result shouldBe Error("You can't withdraw more than the balance.").left()
    }

    "I should be able to withdraw when the balance is enough" {
        Account(AccountId(1)).right()
            .flatMap { deposit(it, 1000.amount(), LocalDate.of(2012, 1, 10)) }
            .flatMap { withdraw(it, 1000.amount(), LocalDate.of(2012, 1, 10)) }
            .isRight() shouldBe true
    }

    "I should not have a negative balance" {
        forAll(Gen.positiveIntegers(), Gen.positiveIntegers()) { initialAmount: Int, amountToWithdraw: Int ->
            Account(AccountId(1)).right()
                .flatMap { deposit(it, initialAmount.amount(), LocalDate.of(2012, 1, 10)) }
                .flatMap { withdraw(it, amountToWithdraw.amount(), LocalDate.of(2012, 1, 10)) }
                .map { balanceLens.get(it).amount >= 0 }
                .getOrElse { true }
        }
    }

})
