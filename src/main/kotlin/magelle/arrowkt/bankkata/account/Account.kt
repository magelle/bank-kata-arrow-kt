package magelle.arrowkt.bankkata.account

import arrow.core.*
import java.time.LocalDate


data class Account(val id: AccountId, val operations: List<Operation> = listOf())

fun withdraw(account: Account, amount: Amount, date: LocalDate): Either<Error, Account> =
    when {
        (balanceLens.get(account) < amount) -> Error("You can't withdraw more than the balance.").left()
        else -> addOperation(account, Withdraw(amount, date)).right()
    }

fun deposit(account: Account, amount: Amount, date: LocalDate): Either<Error, Account> =
    addOperation(account, Deposit(amount, date)).right()

fun transfer(from: Account, to: Account, amount: Amount, date: LocalDate) =
    withdraw(from, amount, date)
        .flatMap { withdrawed -> deposit(to, amount, date)
            .map { deposited -> Tuple2(withdrawed, deposited) } }


private fun addOperation(
    account: Account,
    operation: Operation
) = account.operations
    .plus(operation)
    .let { Account(account.id, it) }

