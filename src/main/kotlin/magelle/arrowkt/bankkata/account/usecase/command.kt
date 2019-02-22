package magelle.arrowkt.bankkata.account.usecase

import arrow.effects.IO
import arrow.effects.instances.io.monad.binding
import magelle.arrowkt.bankkata.account.*
import java.time.LocalDate

fun askForAccountCreation(
    provideAccountId: () -> IO<AccountId>,
    saveAccount: (AccountId, Account) -> IO<AccountId>
) = {
    binding {
        bind {
            provideAccountId()
                .map {
                    saveAccount(it, Account())
                    it
                }
        }
    }
}


fun askForDeposit(
    now: () -> LocalDate,
    getAccount: (AccountId) -> IO<Account>,
    saveAccount: (AccountId, Account) -> IO<AccountId>
) = { accountId: AccountId,
      amount: Amount ->
    binding {
        val account = bind { getAccount(accountId) }
        deposit(account, amount, now())
            .map { bind { saveAccount(accountId, it) } }
    }
}

fun askForWithdrawal(
    now: () -> LocalDate,
    getAccount: (AccountId) -> IO<Account>,
    saveAccount: (AccountId, Account) -> IO<AccountId>
) = { accountId: AccountId,
      amount: Amount ->
    binding {
        val account = bind { getAccount(accountId) }
        withdraw(account, amount, now())
            .map { bind { saveAccount(accountId, it) } }
    }
}