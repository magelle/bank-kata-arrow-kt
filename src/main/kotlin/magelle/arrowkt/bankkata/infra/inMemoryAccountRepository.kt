package magelle.arrowkt.bankkata.infra

import arrow.effects.IO
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.AccountId
import magelle.arrowkt.bankkata.account.accountId
import org.jetbrains.exposed.sql.Database
import java.util.concurrent.atomic.AtomicInteger


private val nextAccountId = AtomicInteger()
private val accounts = mutableMapOf<AccountId, Account>()

val provideAccountId: () -> IO<AccountId> =
    { IO.just(nextAccountId.getAndIncrement().accountId()) }


val saveAccount: (account: Account) -> IO<AccountId> =
    { account: Account ->
        accounts[account.id] = account
        IO.just(account.id)
    }

val getAccount: (accountId: AccountId) -> IO<Account> =
    { accountId: AccountId ->
        accounts[accountId]
            ?.let { IO.just(it) }
            ?: IO.raiseError(RuntimeException("Account with id $accountId not found."))
    }

