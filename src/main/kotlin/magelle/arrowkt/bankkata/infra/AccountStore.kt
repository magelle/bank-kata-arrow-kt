package magelle.arrowkt.bankkata.infra

import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.AccountId

interface AccountStore {
    fun nextAccountId(): AccountId
    fun save(account: Account): AccountId
    fun get(id: AccountId): Account
}