package dev.forsythe.domain

import dev.forsythe.data.model.Expense
import dev.forsythe.data.LocalDb
import dev.forsythe.data.fetchAllExpenses

class ListExpensesUseCase {
    suspend operator fun invoke(limit: Int? = null, category: String? = null) : Result<List<Expense>>{
        return LocalDb
            .fetchAllExpenses()
            .mapCatching { expenses ->
                expenses
                    .filter { if (category == null) true else it.category == category }
                    .sortedBy { it.id }
                    .take(limit ?: Int.MAX_VALUE)
            }
    }

}