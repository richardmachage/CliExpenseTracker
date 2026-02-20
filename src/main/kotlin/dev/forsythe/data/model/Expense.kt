package dev.forsythe.data.model

import java.math.BigDecimal
import java.time.Instant

data class Expense(
    val id : Int,
    val amount : BigDecimal,
    val category : String,
    val description : String? = null,
    val date : Instant
)