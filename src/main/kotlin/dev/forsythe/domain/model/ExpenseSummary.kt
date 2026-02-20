package dev.forsythe.domain.model

import java.math.BigDecimal

data class ExpenseSummary(
    val total: BigDecimal,
    val categoryTotals: Map<String, BigDecimal>
)