package magelle.arrowkt.bankkata.infra

import arrow.effects.IO
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.AccountId
import org.jetbrains.exposed.sql.Transaction

interface AccountStore {
    fun <T> transaction(statement: Transaction.() -> T): T
    fun nextAccountId(): IO<AccountId>
    fun save(account: Account): IO<AccountId>
    fun get(id: AccountId): IO<Account>
}