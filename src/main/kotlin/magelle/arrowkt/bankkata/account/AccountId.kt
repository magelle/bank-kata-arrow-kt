package magelle.arrowkt.bankkata.account

data class AccountId(val id: Int)

fun Int.accountId() = AccountId(this)