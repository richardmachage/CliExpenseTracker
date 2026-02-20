package dev.forsythe.data

import dev.forsythe.data.model.Expense
import java.math.BigDecimal
import java.time.Instant

suspend fun LocalDb.addExpense(
    amount: BigDecimal,
    category: String,
    description: String? = null,
    date: Instant
): Result<Expense> {

    return try {
        this.safeDbOperation {
            val newId = if (listOfExpenses.isEmpty()) 1 else listOfExpenses.maxOf { it.id } + 1
            val newExpense = Expense(
                id = newId,
                category = category,
                amount = amount,
                description = description,
                date = date,
            )
            listOfExpenses.add(newExpense)
            Result.success(newExpense)
        }
    }
    catch (e: Exception) {
        Result.failure(e)
    }

}


suspend fun LocalDb.deleteExpense(id: Int): Result<Boolean> = safeDbOperation {
    try {
        val wasRemoved = listOfExpenses.removeIf { it.id == id }
        Result.success(wasRemoved)
    }
    catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun LocalDb.fetchAllExpenses(): Result<List<Expense>> = safeDbOperation {
    try {
        Result.success(listOfExpenses.toList())
    }
    catch (e: Exception) {
        Result.failure(e)
    }
}


suspend fun LocalDb.clearAll() = safeDbOperation {
    listOfExpenses.clear()
}
