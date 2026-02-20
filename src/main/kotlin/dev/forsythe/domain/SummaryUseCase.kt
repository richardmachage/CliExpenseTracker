package dev.forsythe.domain

import dev.forsythe.data.LocalDb
import dev.forsythe.data.fetchAllExpenses
import dev.forsythe.domain.model.ExpenseSummary

class SummaryUseCase {
    suspend operator fun invoke(): Result<ExpenseSummary> {
        return LocalDb.fetchAllExpenses()
            .mapCatching { expenses ->
            if (expenses.isEmpty()) {
                throw Exception("No expenses found.")
            }

            val total = expenses.sumOf { it.amount }

            val categoryTotals = expenses
                .groupBy { it.category }
                .mapValues { (_, categoryExpenses) ->
                    categoryExpenses.sumOf { it.amount }
                }

                ExpenseSummary(total, categoryTotals)
        }
    }
}