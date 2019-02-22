package magelle.arrowkt.bankkata.infra

import arrow.effects.IO
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.AccountId
import magelle.arrowkt.bankkata.account.accountId
import java.util.concurrent.atomic.AtomicInteger


val nextAccountId = AtomicInteger()
val provideAccountId: () -> IO<AccountId> =
    { IO.just(nextAccountId.getAndIncrement().accountId()) }


val accounts = mutableMapOf<AccountId, Account>()
val saveAccount: (accountId: AccountId, account: Account) -> IO<AccountId> =
    { accountId: AccountId, account: Account ->
        accounts[accountId] = account
        IO.just(accountId)

    }

val getAccount: (accountId: AccountId) -> IO<Account> =
    { accountId: AccountId ->
        accounts[accountId]
            ?.let { IO.just(it) }
            ?: IO.raiseError(RuntimeException("Account with id $accountId not found."))
    }