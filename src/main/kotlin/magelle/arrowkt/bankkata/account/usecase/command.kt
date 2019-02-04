package magelle.arrowkt.bankkata.account.usecase

import arrow.core.Either
import arrow.core.flatMap
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.deposit
import magelle.arrowkt.bankkata.account.withdraw
import java.time.LocalDate

fun askForAccountCreation(
    provideAccountId: () -> Either<String, Int>,
    saveAccount: (Int, Account) -> Either<String, Int>
) = { provideAccountId().flatMap { saveAccount(it, Account()) } }


fun askForDeposit(
    now: () -> LocalDate,
    getAccount: (Int) -> Either<String, Account>,
    saveAccount: (Int, Account) -> Either<String, Int>
) = { accountId: Int,
      amount: Int ->
    getAccount(accountId)
        .flatMap { deposit(it, amount, now()) }
        .flatMap { saveAccount(accountId, it) }
}

fun askForWithdrawal(
    now: () -> LocalDate,
    getAccount: (Int) -> Either<String, Account>,
    saveAccount: (Int, Account) -> Either<String, Int>
) = { accountId: Int,
      amount: Int ->
    getAccount(accountId)
        .flatMap { withdraw(it, amount, now()) }
        .flatMap { saveAccount(accountId, it) }
}