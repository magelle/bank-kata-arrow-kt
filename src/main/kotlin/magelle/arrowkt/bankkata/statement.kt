package magelle.arrowkt.bankkata

import arrow.core.Tuple2
import magelle.arrowkt.bankkata.domain.Account
import magelle.arrowkt.bankkata.domain.Deposit
import magelle.arrowkt.bankkata.domain.Operation
import magelle.arrowkt.bankkata.domain.Withdraw
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun statement(account: Account) =
    account.operations.fold(Tuple2(listOf<Movement>(), 0))
    { acc, operation ->
        val balance = incBalance(acc.b, operation)
        Tuple2(
            acc.a.plus(toMovement(operation, balance)),
            balance
        )
    }.a.reversed()

fun incBalance(balance: Int, operation: Operation) =
    when (operation) {
        is Deposit -> balance + operation.amount
        is Withdraw -> balance - operation.amount
    }

fun toMovement(operation: Operation, balance: Int) =
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

fun format(date: LocalDate) = date.format(DateTimeFormatter.ofPattern("DD/MM/YYYY"))!!

fun format(number: Int) = number.toString()

data class Movement(val date: String, val credit: String, val debit: String, val balance: String)