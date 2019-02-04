package magelle.arrowkt.bankkata.account.usecase

import arrow.core.Either
import magelle.arrowkt.bankkata.account.Movement
import magelle.arrowkt.bankkata.account.Account
import magelle.arrowkt.bankkata.account.statement

fun printStatementQuery(getAccount: (Int) -> Either<String, Account>,
                        accountId: Int): Either<String, List<Movement>> =
    getAccount(accountId).map { statement(it) }