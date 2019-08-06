package magelle.arrowkt.bankkata.account.usecase

import arrow.core.Tuple2
import arrow.core.right
import arrow.data.EitherT
import arrow.data.value
import arrow.effects.ForIO
import arrow.effects.IO
import arrow.effects.fix
import arrow.effects.instances.io.monad.binding
import arrow.effects.instances.io.monad.monad
import arrow.instances.eithert.monad.*
import magelle.arrowkt.bankkata.account.*
import java.time.LocalDate

fun askForAccountCreation(
    provideAccountId: () -> IO<AccountId>,
    saveAccount: (Account) -> IO<AccountId>
) = { ->
    provideAccountId()
        .map { Account(it) }
        .flatMap(saveAccount)
}


fun askForDeposit(
    now: () -> LocalDate,
    getAccount: (AccountId) -> IO<Account>,
    saveAccount: (Account) -> IO<AccountId>
) = { accountId: AccountId,
      amount: Amount ->
    binding {
        val account = bind { getAccount(accountId) }
        deposit(account, amount, now())
            .map { bind { saveAccount(it) } }
    }
}

fun askForWithdrawal(
    now: () -> LocalDate,
    getAccount: (AccountId) -> IO<Account>,
    saveAccount: (Account) -> IO<AccountId>
) = { accountId: AccountId,
      amount: Amount ->
    binding {
        val account = bind { getAccount(accountId) }
        withdraw(account, amount, now())
            .map { bind { saveAccount(it) } }
    }
}

fun askForTransfer(
    now: () -> LocalDate,
    getAccount: (AccountId) -> IO<Account>,
    saveAccount: (Account) -> IO<AccountId>
) = { from: AccountId,
      to: AccountId,
      amount: Amount ->
    EitherT.monad<ForIO, Error>(IO.monad()).binding {
        val debtor = EitherT(getAccount(from).map { it.right() }).bind()
        val creditor = EitherT(getAccount(to).map { it.right() }).bind()
        val (debited, credited) = EitherT(IO.just(transfer(debtor, creditor, amount, now()))).bind()
        val debtorId = EitherT(saveAccount(debited).map { it.right() }).bind()
        val creditorId = EitherT(saveAccount(credited).map { it.right() }).bind()
        Tuple2(debtorId, creditorId)
    }.value().fix()
}


