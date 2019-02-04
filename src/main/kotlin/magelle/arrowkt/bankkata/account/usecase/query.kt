package magelle.arrowkt.bankkata.account.usecase

import arrow.effects.IO
import arrow.effects.instances.io.monad.binding
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.statement

fun printStatementQuery(getAccount: (Int) -> IO<Account>) =
    { accountId: Int ->
        binding {
            var account = bind { getAccount(accountId) }
            statement(account)
        }
    }