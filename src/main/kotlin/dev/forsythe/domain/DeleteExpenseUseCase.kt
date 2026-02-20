package dev.forsythe.domain

import dev.forsythe.data.LocalDb
import dev.forsythe.data.deleteExpense

class DeleteExpenseUseCase {
    suspend operator fun invoke(id: Int)  : Result<Unit> {
        return LocalDb.deleteExpense(id)
            .mapCatching { wasRemoved ->
                if (wasRemoved.not()) throw Exception("Expense with ID $id not found.")
            }
    }

}