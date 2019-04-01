package magelle.arrowkt.bankkata.account

data class Amount(val amount: Int) {
    operator fun compareTo(other: Amount) = this.amount.compareTo(other.amount)
    operator fun plus(other: Amount) =
        Amount(this.amount + other.amount)
    operator fun minus(other: Amount) =
        Amount(this.amount - other.amount)
    override fun toString() = amount.toString()
}

fun Int.amount() = Amount(this)