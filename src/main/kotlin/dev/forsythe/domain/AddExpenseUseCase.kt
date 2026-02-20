package dev.forsythe.domain

import dev.forsythe.data.LocalDb
import dev.forsythe.data.addExpense
import java.math.BigDecimal
import java.time.Instant

class AddExpenseUseCase {
    suspend operator fun invoke(
        amount: BigDecimal,
        category: String,
        description: String? = null,
        date: Instant? = null
    )  = LocalDb.addExpense(
        amount = amount,
        category = category.ifBlank { "Uncategorized" },
        description = description?.ifBlank { null },
        date = date ?: Instant.now()
    )
}