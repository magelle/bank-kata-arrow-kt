package magelle.arrowkt.bankkata.domain

import java.time.LocalDate

sealed class Operation
data class Deposit(val amount: Int, val date: LocalDate) : Operation()
data class Withdraw(val amount: Int, val date: LocalDate) : Operation()

data class Account(val operations: List<Operation> = listOf())

fun withdraw(account: Account, amount: Int, date: LocalDate) =
    account.copy(
        operations = account.operations.plus(Withdraw(amount, date))
    )

fun deposit(account: Account, amount: Int, date: LocalDate) =
    account.copy(
        operations = account.operations.plus(Deposit(amount, date))
    )


