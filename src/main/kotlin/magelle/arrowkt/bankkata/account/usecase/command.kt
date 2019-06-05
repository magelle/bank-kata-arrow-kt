package magelle.arrowkt.bankkata.account.usecase

import arrow.core.Tuple2
import arrow.effects.IO
import arrow.effects.instances.io.monad.binding
import magelle.arrowkt.bankkata.account.*
import java.time.LocalDate

fun askForAccountCreation(
    provideAccountId: () -> IO<AccountId>,
    saveAccount: (Account) -> IO<AccountId>
) = { ->
    provideAccountId()
        .map { Account(it) }
        .flatMap(saveAccount)
}


fun askForDeposit(
    now: () -> LocalDate,
    getAccount: (AccountId) -> IO<Account>,
    saveAccount: (Account) -> IO<AccountId>
) = { accountId: AccountId,
      amount: Amount ->
    binding {
        val account = bind { getAccount(accountId) }
        deposit(account, amount, now())
            .map { bind { saveAccount(it) } }
    }
}

fun askForWithdrawal(
    now: () -> LocalDate,
    getAccount: (AccountId) -> IO<Account>,
    saveAccount: (Account) -> IO<AccountId>
) = { accountId: AccountId,
      amount: Amount ->
    binding {
        val account = bind { getAccount(accountId) }
        withdraw(account, amount, now())
            .map { bind { saveAccount(it) } }
    }
}

fun askForTransfer(
    now: () -> LocalDate,
    getAccount: (AccountId) -> IO<Account>,
    saveAccount: (Account) -> IO<AccountId>
) = { from: AccountId,
      to: AccountId,
      amount: Amount ->
    binding {
        val (fromAccount, toAccount) = bind { get2(getAccount, from, to) }
        transfer(fromAccount, toAccount, amount, now())
            .map { (minusAccount, plusAccount) ->
                bind { save2(saveAccount, minusAccount, plusAccount) }
            }
    }
}

fun get2(
    getAccount: (AccountId) -> IO<Account>,
    accountId1: AccountId,
    accountId2: AccountId
) =
    binding {
        Tuple2(
            bind { getAccount(accountId1) },
            bind { getAccount(accountId2) }
        )
    }

fun save2(
    saveAccount: (Account) -> IO<AccountId>,
    account1: Account,
    account2: Account
) =
    binding {
        Tuple2(
            bind { saveAccount(account1) },
            bind { saveAccount(account2) }
        )
    }

