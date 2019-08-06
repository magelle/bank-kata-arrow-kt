package magelle.arrowkt.bankkata

import arrow.core.left
import arrow.effects.instances.io.monad.binding
import arrow.syntax.function.curried
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import magelle.arrowkt.bankkata.account.*
import magelle.arrowkt.bankkata.account.usecase.*
import magelle.arrowkt.bankkata.infra.getAccount
import magelle.arrowkt.bankkata.infra.h2AccountStore
import magelle.arrowkt.bankkata.infra.provideAccountId
import magelle.arrowkt.bankkata.infra.saveAccount
import java.time.LocalDate

class AccountIntegrationTests : StringSpec({
    "should be able to make deposit, withdrawal and get statement" {
        h2AccountStore.transaction {
            binding {
                val accountId = bind { createAccount() }

                nowIs(LocalDate.of(2012, 1, 10))
                bind { makeDeposit(accountId) (1_000.amount()) }
                nowIs(LocalDate.of(2012, 1, 13))
                bind { makeDeposit(accountId) (2_000.amount()) }
                nowIs(LocalDate.of(2012, 1, 14))
                bind { makeWithdrawal(accountId) (500.amount()) }
                bind { printStatement(accountId) }
            }.unsafeRunSync() shouldBe listOf(
                Movement(date = "14/01/2012", credit = "", debit = "500", balance = "2500"),
                Movement(date = "13/01/2012", credit = "2000", debit = "", balance = "3000"),
                Movement(date = "10/01/2012", credit = "1000", debit = "", balance = "1000")
            )
        }
    }

    "deposit should update balance" {
        h2AccountStore.transaction {
            binding {
                val accountId = bind { createAccount() }
                bind { makeDeposit(accountId)(1_000.amount()) }
                bind { balance(accountId) } shouldBe 1_000.amount()
            }.unsafeRunSync()
        }
    }

    "withdrawal should update balance" {
        h2AccountStore.transaction {
            binding {
                val accountId = bind { createAccount() }
                bind { makeDeposit(accountId)(1_000.amount()) }
                bind { makeWithdrawal(accountId) (100.amount()) }
                bind { balance(accountId) } shouldBe 900.amount()
            }.unsafeRunSync()
        }
    }

    "should not allow to withdraw more than you have" {
        h2AccountStore.transaction {
            binding {
                val accountId = bind { createAccount() }
                bind { makeDeposit(accountId)(1_000.amount()) }
                bind { makeWithdrawal(accountId) (1001.amount()) }
            }.unsafeRunSync() shouldBe Error("You can't withdraw more than the balance.").left()
        }
    }

    "Should allow to withdraw all the money" {
        h2AccountStore.transaction {
            binding {
                val accountId = bind { createAccount() }
                bind { makeDeposit(accountId)(1_000.amount()) }
                bind { makeWithdrawal(accountId) (1_000.amount()) }
            }.unsafeRunSync().isRight() shouldBe true
        }
    }

    "Should make a transaction between two account" {
        h2AccountStore.transaction {
            binding {
                val senderAccountId = bind { createAccount() }
                val receiverAccountId = bind { createAccount() }
                bind { makeDeposit(senderAccountId)(1_000.amount()) }
                bind { balance(senderAccountId) } shouldBe 1_000.amount()
                bind { balance(receiverAccountId) } shouldBe 0.amount()
                bind { makeTransfer(senderAccountId) (receiverAccountId) (300.amount()) }
                bind { balance(senderAccountId) } shouldBe 700.amount()
                bind { balance(receiverAccountId) } shouldBe 300.amount()
            }.unsafeRunSync()
        }
    }
})


val createAccount = ::askForAccountCreation.curried() (provideAccountId) (saveAccount)
val makeDeposit = ::askForDeposit.curried() (now) (getAccount) (saveAccount)
val makeWithdrawal = ::askForWithdrawal.curried() (now) (getAccount) (saveAccount)
val makeTransfer = ::askForTransfer.curried() (now) (getAccount) (saveAccount)

val balance = ::getBalance.curried() (getAccount)
val printStatement = ::printStatementQuery.curried() (getAccount)
