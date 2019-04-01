package magelle.arrowkt.bankkata.infra

import arrow.effects.IO
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.AccountId

interface AccountStore {
    fun nextAccountId(): IO<AccountId>
    fun save(account: Account): IO<AccountId>
    fun get(id: AccountId): IO<Account>
}