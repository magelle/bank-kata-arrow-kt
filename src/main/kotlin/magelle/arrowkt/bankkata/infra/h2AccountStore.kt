package magelle.arrowkt.bankkata.infra

import arrow.effects.IO
import magelle.arrowkt.bankkata.account.*
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDate

val h2AccountStore = object : AccountStore {

    private val connection =
        Database.connect(
            url = "jdbc:h2:./build/account",
            driver = "org.h2.Driver",
            user = "sa"
        )

    init {
        transaction(connection) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Operations)
            SchemaUtils.create(Accounts)
        }
    }

    override fun <T> transaction(statement: Transaction.() -> T): T {
        return transaction(connection, statement)
    }

    override fun nextAccountId() = IO {
        Accounts.insert { }
        Accounts.selectAll()
            .orderBy(Accounts.id, SortOrder.DESC)
            .limit(1)
            .map { it[Accounts.id] }
            .firstOrNull()
            ?.value?.accountId()
            ?: AccountId(1)
    }

    override fun get(id: AccountId) = IO {
        retrieveAccount(id)
    }

    private fun retrieveAccount(id: AccountId) =
        Account(id, retrieveOperations(id))

    override fun save(account: Account) = IO {
        val operationsOfAccount = account.operations
        val existingOperations = retrieveOperations(account.id)
        val operationsToSave = operationsOfAccount - existingOperations
        insertOperations(account.id, operationsToSave)
        account.id
    }

    private fun retrieveOperations(id: AccountId) =
        Operations.select { Operations.accountId eq id.id }
            .map {
                println(it)
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

    private fun insertOperations(id: AccountId, operations: List<Operation>) =
        Operations.batchInsert(operations) {
            this[Operations.accountId] = id.id
            this[Operations.type] = operationType(it)
            this[Operations.amount] = amount(it).amount
            this[Operations.date] = date(it).toEpochDay()
        }
}

object Accounts : IntIdTable("accounts") {}
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
