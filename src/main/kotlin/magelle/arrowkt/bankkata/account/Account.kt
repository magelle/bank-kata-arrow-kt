package magelle.arrowkt.bankkata.account

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import arrow.optics.Getter
import java.time.LocalDate


data class Error(val message: String)

data class Amount(private val amount: Int) {
    operator fun compareTo(other: Amount) = this.amount.compareTo(other.amount)
    operator fun plus(other: Amount) = Amount(this.amount + other.amount)
    operator fun minus(other: Amount) = Amount(this.amount - other.amount)
    override fun toString() = amount.toString()
}
fun Int.amount() = Amount(this)

data class AccountId(private val id: Int)
fun Int.accountId() = AccountId(this)

sealed class Operation
data class Deposit(val amount: Amount, val date: LocalDate) : Operation()
data class Withdraw(val amount: Amount, val date: LocalDate) : Operation()

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

fun incBalance(balance: Amount, operation: Operation) =
    when (operation) {
        is Deposit -> balance + operation.amount
        is Withdraw -> balance - operation.amount
    }

val balanceLens: Getter<Account, Amount> =
    Getter { account ->
        account.operations.fold(0.amount())
        { balance, operation -> incBalance(balance, operation) }
    }
