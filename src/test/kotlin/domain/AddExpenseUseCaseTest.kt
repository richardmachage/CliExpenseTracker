package domain

import dev.forsythe.data.LocalDb
import dev.forsythe.data.clearAll
import dev.forsythe.data.fetchAllExpenses
import dev.forsythe.domain.AddExpenseUseCase
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AddExpenseUseCaseTest {
    private val addExpenseUseCase = AddExpenseUseCase()

    @BeforeTest
    fun setup() = runBlocking {
        LocalDb.clearAll()
    }

    @Test
    fun `successfully adds expense and returns generated ID`() = runBlocking {
        val result = addExpenseUseCase(
            amount = BigDecimal("50.0"),
            category = "Food",
            description = "Lunch"
        )
        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.id)
    }

    @Test
    fun `applies business rule defaulting date and blank category`() = runBlocking {
        addExpenseUseCase(amount = BigDecimal("10.0"), category = "   ")

        val expenses = LocalDb.fetchAllExpenses().getOrThrow()
        assertEquals(1, expenses.size)
        assertEquals("Uncategorized", expenses.first().category)
        assertNotNull(expenses.first().date) // Proves the Instant.now() fallback worked
        Unit
    }

    @Test
    fun `applies business rule defaulting blank description to null`() = runBlocking {
        addExpenseUseCase(
            amount = BigDecimal("10.0"),
            category = "Food",
            description = "   " // Blank description
        )

        val expenses = LocalDb.fetchAllExpenses().getOrThrow()
        assertEquals(1, expenses.size)
        assertEquals(null, expenses.first().description)
    }
}