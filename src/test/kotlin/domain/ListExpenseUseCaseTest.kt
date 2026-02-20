package domain

import dev.forsythe.data.LocalDb
import dev.forsythe.data.addExpense
import dev.forsythe.data.clearAll
import dev.forsythe.domain.ListExpensesUseCase
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListExpenseUseCaseTest {
    private val listExpensesUseCase = ListExpensesUseCase()

    @BeforeTest
    fun setup() = runBlocking {
        LocalDb.clearAll()

        //populate test data
        LocalDb.addExpense(BigDecimal("10.0"), "Food", "A", Instant.now())
        LocalDb.addExpense(BigDecimal("20.0"), "Transport", "B", Instant.now())
        LocalDb.addExpense(BigDecimal("30.0"), "Food", "C", Instant.now())

        Unit
    }

    @Test
    fun `returns all expenses when no filters are applied`() = runBlocking {
        val result = listExpensesUseCase()
        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrThrow().size)
    }

    @Test
    fun `correctly filters by category`() = runBlocking {
        val result = listExpensesUseCase(category = "Food")
        val expenses = result.getOrThrow()

        assertEquals(2, expenses.size)
        assertTrue(expenses.all { it.category == "Food" })
    }

    @Test
    fun `correctly limits the output size`() = runBlocking {
        val result = listExpensesUseCase(limit = 2)
        assertEquals(2, result.getOrThrow().size)
    }

    @Test
    fun `correctly applies both limit and category filters simultaneously`() = runBlocking {
        val result = listExpensesUseCase(limit = 1, category = "Food")
        val expenses = result.getOrThrow()

        assertEquals(1, expenses.size)
        assertEquals("Food", expenses.first().category)
        assertEquals("A", expenses.first().description) // Ensures it grabbed the first one
    }

    @Test
    fun `returns empty list when filtering by non-existent category`() = runBlocking {
        val result = listExpensesUseCase(category = "Housing")
        val expenses = result.getOrThrow()

        assertTrue(result.isSuccess)
        assertTrue(expenses.isEmpty())
    }

    @Test
    fun `handles oversized limit gracefully without throwing exceptions`() = runBlocking {
        val result = listExpensesUseCase(limit = 100)
        val expenses = result.getOrThrow()

        assertEquals(3, expenses.size) // Just returns whatever is available
    }
}