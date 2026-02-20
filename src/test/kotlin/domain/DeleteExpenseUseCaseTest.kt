package domain

import dev.forsythe.data.LocalDb
import dev.forsythe.data.addExpense
import dev.forsythe.data.clearAll
import dev.forsythe.data.fetchAllExpenses
import dev.forsythe.domain.DeleteExpenseUseCase
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteExpenseUseCaseTest {
    private val deleteExpenseUseCase = DeleteExpenseUseCase()

    @BeforeTest
    fun setup() = runBlocking {
        LocalDb.clearAll()
    }

    @Test
    fun `returns success when deleting an existing ID`() = runBlocking {
        //add data to ensure it exists
        LocalDb.addExpense(BigDecimal("100.0"), "Food", "Lunch", Instant.now())

        // deletion
        val result = deleteExpenseUseCase(1)
        assertTrue(result.isSuccess)

        val expenses = LocalDb.fetchAllExpenses().getOrThrow()
        assertTrue(expenses.isEmpty())
    }


    @Test
    fun `returns failure exception when deleting a non-existent ID`() = runBlocking {
        val result = deleteExpenseUseCase(99)
        assertTrue(result.isFailure)
        assertEquals("Expense with ID 99 not found.", result.exceptionOrNull()?.message)
    }


    @Test
    fun `maintains list integrity when deleting a specific ID`() = runBlocking {
        // Setup 3 items
        LocalDb.addExpense(BigDecimal("10.0"), "Food", "A", Instant.now()) // ID 1
        LocalDb.addExpense(BigDecimal("20.0"), "Transport", "B", Instant.now()) // ID 2
        LocalDb.addExpense(BigDecimal("30.0"), "Food", "C", Instant.now()) // ID 3

        // Delete the middle one
        val result = deleteExpenseUseCase(2)
        assertTrue(result.isSuccess)

        // Verify 1 and 3 are intact, and 2 is gone
        val expenses = LocalDb.fetchAllExpenses().getOrThrow()
        assertEquals(2, expenses.size)
        assertTrue(expenses.any { it.id == 1 })
        assertTrue(expenses.any { it.id == 3 })
        assertTrue(expenses.none { it.id == 2 })
    }
}