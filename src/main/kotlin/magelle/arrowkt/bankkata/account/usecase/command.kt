package magelle.arrowkt.bankkata.account.usecase

import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.instances.io.monad.binding
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.deposit
import magelle.arrowkt.bankkata.account.withdraw
import java.time.LocalDate

fun askForAccountCreation(
    provideAccountId: () -> IO<Int>,
    saveAccount: (Int, Account) -> IO<Int>
) = {
    binding {
        val accountId = bind { provideAccountId() }
        saveAccount(accountId, Account())
        accountId
    }.fix().unsafeRunSync()
}


fun askForDeposit(
    now: () -> LocalDate,
    getAccount: (Int) -> IO<Account>,
    saveAccount: (Int, Account) -> IO<Int>
) = { accountId: Int,
      amount: Int ->
    binding {
        val account = bind { getAccount(accountId) }
        deposit(account, amount, now())
            .map { bind { saveAccount(accountId, it) } }
    }.fix().unsafeRunSync()
}

fun askForWithdrawal(
    now: () -> LocalDate,
    getAccount: (Int) -> IO<Account>,
    saveAccount: (Int, Account) -> IO<Int>
) = { accountId: Int,
      amount: Int ->
    binding {
        val account = bind { getAccount(accountId) }
        withdraw(account, amount, now())
            .map { bind { saveAccount(accountId, it) } }
    }.fix().unsafeRunSync()
}