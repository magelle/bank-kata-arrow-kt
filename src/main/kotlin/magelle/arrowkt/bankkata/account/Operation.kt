package magelle.arrowkt.bankkata.account

import java.time.LocalDate

sealed class Operation
data class Deposit(val amount: Amount, val date: LocalDate) : Operation()
data class Withdraw(val amount: Amount, val date: LocalDate) : Operation()