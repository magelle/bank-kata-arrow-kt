package magelle.arrowkt.bankkata.infra

import arrow.effects.IO
import magelle.arrowkt.bankkata.account.Account
import java.util.concurrent.atomic.AtomicInteger


val nextAccountId = AtomicInteger()
val provideAccountId: () -> IO<Int> =
    { IO.just(nextAccountId.getAndIncrement()) }


val accounts = mutableMapOf<Int, Account>()
val saveAccount: (accountId: Int, account: Account) -> IO<Int> =
    { accountId: Int, account: Account ->
        accounts[accountId] = account
        IO.just(accountId)

    }

val getAccount: (accountId: Int) -> IO<Account> =
    { accountId: Int ->
        accounts[accountId]
            ?.let { IO.just(it) }
            ?: IO.raiseError<Account>(RuntimeException("Account with id $accountId not found."))
    }