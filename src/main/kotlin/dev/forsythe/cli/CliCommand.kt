package dev.forsythe.cli

import java.math.BigDecimal
import java.time.Instant

sealed class CliCommand {
    data class AddExpense(
        val amount: BigDecimal,
        val category: String,
        val description: String? = null,
        val date: Instant? = null
    ) : CliCommand()

    data class Delete(val id: Int) : CliCommand()
    data class List(val limit: Int? = null, val category: String? = null) : CliCommand()

    data object Summary : CliCommand()
    data object Unknown : CliCommand()
    data class Invalid(val message: String) : CliCommand()
}


