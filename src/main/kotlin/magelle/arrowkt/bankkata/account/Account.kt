package magelle.arrowkt.bankkata.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.time.LocalDate


data class Account(val id: AccountId, val operations: List<Operation> = listOf())

fun withdraw(account: Account, amount: Amount, date: LocalDate): Either<Error, Account> =
    when {
        (balanceLens.get(account) < amount) -> Error("You can't withdraw more than the balance.").left()
        else -> addOperation(account, Withdraw(amount, date)).right()
    }

fun deposit(account: Account, amount: Amount, date: LocalDate): Either<Error, Account> =
    addOperation(account, Deposit(amount, date)).right()

private fun addOperation(
    account: Account,
    operation: Operation
) = account.operations
    .plus(operation)
    .let { Account(account.id, it) }

