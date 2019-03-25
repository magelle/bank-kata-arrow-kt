package magelle.arrowkt.bankkata.infra

import magelle.arrowkt.bankkata.account.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicInteger

val h2AccountStore = object : AccountStore {
    private val nextAccountId = AtomicInteger()
    private val connection =
        Database.connect(
            url = "jdbc:h2:~/account",
            driver = "org.h2.Driver",
            user = "sa"
        )

    init {
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Operations)
        }
    }

    override fun nextAccountId() =
        nextAccountId.incrementAndGet().accountId()

    override fun get(id: AccountId) = transaction {
        retrieveAccount(id)
    }

    private fun retrieveAccount(id: AccountId) =
        Account(id, retrieveOperations(id))

    override fun save(account: Account) =
        transaction {
            addLogger(StdOutSqlLogger)
            deleteAllOperations(account.id)
            insertOperations(account)
            account.id
        }

    private fun retrieveOperations(id: AccountId) =
        Operations.select { equalsAccountId(id) }.map {
            when (it[Operations.type]) {
                OperationType.DEPOSIT -> Deposit(
                    Amount(it[Operations.amount]),
                    LocalDate.ofEpochDay(it[Operations.date])
                )
                OperationType.WITHDRAWAL -> Withdraw(
                    Amount(it[Operations.amount]),
                    LocalDate.ofEpochDay(it[Operations.date])
                )
            }
        }

    private fun insertOperations(account: Account) =
        Operations.batchInsert(account.operations) {
            this[Operations.accountId] = account.id.id
            this[Operations.type] = operationType(it)
            this[Operations.amount] = amount(it).amount
            this[Operations.date] = date(it).toEpochDay()
        }

    private fun deleteAllOperations(id: AccountId) =
        Operations.deleteWhere { equalsAccountId(id) }

    private fun equalsAccountId(accountId: AccountId) = Operations.accountId eq accountId.id

    private fun <T> transaction(statement: Transaction.() -> T) =
        transaction(connection, statement)
}

object Operations : Table("operations") {
    val accountId = integer("account_id")
    val type = enumeration("type", OperationType::class)
    val amount = integer("amount")
    val date = long("date")
}

enum class OperationType { DEPOSIT, WITHDRAWAL }

fun operationType(operation: Operation) = when (operation) {
    is Deposit -> OperationType.DEPOSIT
    is Withdraw -> OperationType.WITHDRAWAL
}

fun amount(operation: Operation) = when (operation) {
    is Deposit -> operation.amount
    is Withdraw -> operation.amount
}

fun date(operation: Operation) = when (operation) {
    is Deposit -> operation.date
    is Withdraw -> operation.date
}