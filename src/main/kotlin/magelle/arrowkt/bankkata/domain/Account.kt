package magelle.arrowkt.bankkata.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.optics.Getter
import java.time.LocalDate

sealed class Operation
data class Deposit(val amount: Int, val date: LocalDate) : Operation()
data class Withdraw(val amount: Int, val date: LocalDate) : Operation()

data class Account(val operations: List<Operation> = listOf())

fun withdraw(account: Account, amount: Int, date: LocalDate): Either<String, Account> {
    return when {
        (balanceLens.get(account) < amount) -> return "You can't withdraw more than the balance.".left()
        else -> addOperation(account, Withdraw(amount, date)).right()
    }
}

fun deposit(account: Account, amount: Int, date: LocalDate): Either<String, Account> =
    addOperation(account, Deposit(amount, date)).right()

private fun addOperation(
    account: Account,
    operation: Operation
) = account.operations
    .plus(operation)
    .let { Account(it) }

fun incBalance(balance: Int, operation: Operation) =
    when (operation) {
        is Deposit -> balance.plus(operation.amount)
        is Withdraw -> balance.minus(operation.amount)
    }

val balanceLens: Getter<Account, Int> =
    Getter { account ->
        account.operations.fold(0)
        { balance, operation -> incBalance(balance, operation) }
    }
