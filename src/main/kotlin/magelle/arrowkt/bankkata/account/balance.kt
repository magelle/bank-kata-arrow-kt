package magelle.arrowkt.bankkata.account

import arrow.optics.Getter

fun foldBalance(balance: Amount, operation: Operation) =
    when (operation) {
        is Deposit -> balance + operation.amount
        is Withdraw -> balance - operation.amount
    }

val balanceLens: Getter<Account, Amount> =
    Getter { account ->
        account.operations.fold(0.amount())
        { balance, operation -> foldBalance(balance, operation) }
    }