package magelle.arrowkt.bankkata.infra

import arrow.effects.IO
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.AccountId


val provideAccountId: () -> IO<AccountId> =
    { IO.just(h2AccountStore.nextAccountId()) }


val saveAccount: (account: Account) -> IO<AccountId> =
    { account: Account -> IO.just(h2AccountStore.save(account)) }

val getAccount: (accountId: AccountId) -> IO<Account> =
    { accountId: AccountId -> IO.just(h2AccountStore.get(accountId)) }
