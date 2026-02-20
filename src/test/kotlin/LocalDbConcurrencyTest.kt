import dev.forsythe.data.LocalDb
import dev.forsythe.data.addExpense
import dev.forsythe.data.clearAll
import dev.forsythe.data.deleteExpense
import dev.forsythe.data.fetchAllExpenses
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


/**
 * Stress tests for the In-memory LocalDb
 * * Note: While a single-user CLI application processing sequential standard input
 * will not practically encounter high-concurrency race conditions, these tests
 * exist to prove the integrity of the Mutex locks.
 * By simulating massive thread contention using Dispatchers.Default, we verify that the data
 * layer is genuinely thread-safe and free of race conditions. This ensures the
 * underlying data architecture is robust enough to be lifted and shifted into a concurrent environment
 * like a real world Android application cache without requiring structural rewrites.
 */

class LocalDbConcurrencyTest {
    @BeforeTest
    fun setup() = runBlocking {
        LocalDb.clearAll()
    }

    @Test
    fun `concurrent adds do not lose data and generate strictly unique IDs`() = runBlocking {
        val numberOfCoroutines = 1000

        // Push work to a multi-threaded pool to force actual race conditions
        withContext(Dispatchers.Default) {
            val deferred = (1..numberOfCoroutines).map { i ->
                async {
                    LocalDb.addExpense(
                        amount = BigDecimal(i),
                        category = "StressTest",
                        description = "Desc $i",
                        date = Instant.now()
                    )
                }
            }
            deferred.awaitAll()
        }

        val expenses = LocalDb.fetchAllExpenses().getOrThrow()

        // If the Mutex lock is missing or flawed, the list size will be less than 1000
        // due to threads overwriting each other's inserts.
        assertEquals(numberOfCoroutines, expenses.size)

        // If ID generation isn't atomic inside the lock, we will get duplicate IDs.
        val uniqueIds = expenses.map { it.id }.toSet()
        assertEquals(numberOfCoroutines, uniqueIds.size)
    }

    @Test
    fun `concurrent deletes handle lock contention safely without crashing`() = runBlocking {
        val itemCount = 100

        // add some date to the database sequentially
        for (i in 1..itemCount) {
            LocalDb.addExpense(BigDecimal.TEN, "Cat", null, Instant.now())
        }

        //Spawn 100 threads to delete all items simultaneously
        withContext(Dispatchers.Default) {
            val deferred = (1..itemCount).map { id ->
                async {
                    LocalDb.deleteExpense(id)
                }
            }
            deferred.awaitAll()
        }

        // Verification for the db survived the onslaught and is perfectly empty
        val expenses = LocalDb.fetchAllExpenses().getOrThrow()
        assertTrue(expenses.isEmpty())
    }
}