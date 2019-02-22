package magelle.arrowkt.bankkata.account

import arrow.core.Tuple2
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun statement(account: Account) =
    account.operations.fold(Tuple2(listOf<Movement>(), 0.amount()))
    { acc, operation ->
        val balance = incBalance(acc.b, operation)
        Tuple2(
            acc.a.plus(toMovement(operation, balance)),
            balance
        )
    }.a.reversed()

private fun toMovement(operation: Operation, balance: Amount) =
    when (operation) {
        is Deposit -> Movement(
            format(operation.date),
            format(operation.amount),
            "",
            balance.toString()
        )
        is Withdraw -> Movement(
            format(operation.date),
            "",
            format(operation.amount),
            balance.toString()
        )
    }

private fun format(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("DD/MM/YYYY"))!!

private fun format(amount: Amount) = amount.toString()

data class Movement(val date: String, val credit: String, val debit: String, val balance: String)
