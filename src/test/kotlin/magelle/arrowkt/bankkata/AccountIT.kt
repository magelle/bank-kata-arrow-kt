package magelle.arrowkt.bankkata

import arrow.core.left
import arrow.effects.instances.io.monad.binding
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import magelle.arrowkt.bankkata.account.Movement
import magelle.arrowkt.bankkata.account.amount
import magelle.arrowkt.bankkata.account.usecase.askForAccountCreation
import magelle.arrowkt.bankkata.account.usecase.askForDeposit
import magelle.arrowkt.bankkata.account.usecase.askForWithdrawal
import magelle.arrowkt.bankkata.account.usecase.printStatementQuery
import magelle.arrowkt.bankkata.infra.getAccount
import magelle.arrowkt.bankkata.infra.provideAccountId
import magelle.arrowkt.bankkata.infra.saveAccount
import java.time.LocalDate

class AccountTests : StringSpec({
    "should be able to make deposit, withdrawal and get statement" {
        binding {
            val accountId = bind { createAccount() }

            nowIs(LocalDate.of(2012, 1, 10))
            bind { makeDeposit(accountId, 1000.amount()) }
            nowIs(LocalDate.of(2012, 1, 13))
            bind { makeDeposit(accountId, 2000.amount()) }
            nowIs(LocalDate.of(2012, 1, 14))
            bind { makeWithdrawal(accountId, 500.amount()) }
            bind { printStatement(accountId) }
        }.unsafeRunSync() shouldBe listOf(
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

    "should not allow to withdraw more than you have" {
        binding {
            val accountId = bind { createAccount() }
            bind { makeDeposit(accountId, 1000.amount()) }
            bind { makeWithdrawal(accountId, 1001.amount()) }
        }.unsafeRunSync() shouldBe "You can't withdraw more than the balance.".left()
    }

    "Should allow to withdraw all the money" {
        binding {
            val accountId = bind { createAccount() }
            bind { makeDeposit(accountId, 1000.amount()) }
            bind { makeWithdrawal(accountId, 1000.amount()) }
        }.unsafeRunSync().isRight() shouldBe true
    }
})


val createAccount = askForAccountCreation(provideAccountId, saveAccount)
val makeDeposit = askForDeposit(now, getAccount, saveAccount)
val makeWithdrawal = askForWithdrawal(now, getAccount, saveAccount)
val printStatement = printStatementQuery(getAccount)