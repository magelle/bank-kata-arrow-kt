package magelle.arrowkt.bankkata.infra

import arrow.effects.IO
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.AccountId


val provideAccountId: () -> IO<AccountId> =
    { h2AccountStore.nextAccountId() }

val saveAccount: (account: Account) -> IO<AccountId> =
    { account: Account -> h2AccountStore.save(account) }

val getAccount: (accountId: AccountId) -> IO<Account> =
    { accountId: AccountId -> h2AccountStore.get(accountId) }
