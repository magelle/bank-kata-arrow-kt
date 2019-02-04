package magelle.arrowkt.bankkata.infra

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import magelle.arrowkt.bankkata.account.Account
import java.util.concurrent.atomic.AtomicInteger


val nextAccountId = AtomicInteger()
val provideAccountId: () -> Either<Nothing, Int> =
    { nextAccountId.getAndIncrement().right() }


val accounts = mutableMapOf<Int, Account>()
val saveAccount: (accountId: Int, account: Account) -> Either<Nothing, Int> =
    { accountId: Int, account: Account ->
        accounts[accountId] = account
        accountId.right()
    }
val getAccount: (accountId: Int) -> Either<String, Account> =
    { accountId: Int ->
        accounts[accountId]
            ?.right()
            ?: "Account with id $accountId not found.".left() }