package domain

import dev.forsythe.data.LocalDb
import dev.forsythe.data.addExpense
import dev.forsythe.data.clearAll
import dev.forsythe.domain.SummaryUseCase
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SummaryUseCaseTest {
    private val summaryUseCase = SummaryUseCase()

    @BeforeTest
    fun setup() = runBlocking {
        // clean in memory db before every test
        LocalDb.clearAll()
    }

    @Test
    fun `returns failure when no expense exists`() = runBlocking {
        val result = summaryUseCase()
        assertTrue (result.isFailure)
        assertEquals("No expenses found.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `calculates correct total and category groupings`() = runBlocking {
        // add some known data
        LocalDb.addExpense(BigDecimal("100.50"), "Food", "Lunch", Instant.now())
        LocalDb.addExpense(BigDecimal("50.00"), "Food", "Snacks", Instant.now())
        LocalDb.addExpense(BigDecimal("200.00"), "Transport", "Bus", Instant.now())

        //execute
        val result = summaryUseCase()

        //check the computation
        assertTrue (result.isSuccess)
        val summary = result.getOrThrow()


        //Total should be 350.50
        assertEquals(BigDecimal("350.50"), summary.total)

        //categories should be grouped correctly
        assertEquals(2, summary.categoryTotals.size)

        assertEquals(BigDecimal("150.50"), summary.categoryTotals["Food"])
        assertEquals(BigDecimal("200.00"), summary.categoryTotals["Transport"])

    }

}