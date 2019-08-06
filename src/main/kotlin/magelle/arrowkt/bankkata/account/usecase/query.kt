package magelle.arrowkt.bankkata.account.usecase

import arrow.effects.IO
import arrow.effects.instances.io.monad.binding
import magelle.arrowkt.bankkata.account.*

fun printStatementQuery(getAccount: (AccountId) -> IO<Account>, accountId: AccountId): IO<List<Movement>> =
    binding {
        val account = bind { getAccount(accountId) }
        statement(account)
    }

fun getBalance(getAccount: (AccountId) -> IO<Account>, accountId: AccountId): IO<Amount> =
    getAccount(accountId).map { balanceLens.get(it) }