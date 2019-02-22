package magelle.arrowkt.bankkata.account.usecase

import arrow.effects.IO
import arrow.effects.instances.io.monad.binding
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.AccountId
import magelle.arrowkt.bankkata.account.statement

fun printStatementQuery(getAccount: (AccountId) -> IO<Account>) =
    { accountId: AccountId ->
        binding {
            val account = bind { getAccount(accountId) }
            statement(account)
        }
    }