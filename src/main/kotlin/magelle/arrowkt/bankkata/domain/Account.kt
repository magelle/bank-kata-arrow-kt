package magelle.arrowkt.bankkata.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import java.time.LocalDate

sealed class Operation
data class Deposit(val amount: Int, val date: LocalDate) : Operation()
data class Withdraw(val amount: Int, val date: LocalDate) : Operation()

data class Account(val operations: List<Operation> = listOf())

fun withdraw(account: Account, amount: Int, date: LocalDate): Either<String, Account> {
    if (balance(account) < amount) return "You can't withdraw more than the balance.".left()
    return Account(
        operations = account.operations.plus(Withdraw(amount, date))
    ).right()
}

fun deposit(account: Account, amount: Int, date: LocalDate): Either<String, Account> =
    Account(
        operations = account.operations.plus(Deposit(amount, date))
    ).right()

fun balance(account: Account) = account.operations
    .fold(0) { balance, cur -> incBalance(balance, cur) }

fun incBalance(balance: Int, operation: Operation) =
    when (operation) {
        is Deposit -> balance.plus(operation.amount)
        is Withdraw -> balance.minus(operation.amount)
    }