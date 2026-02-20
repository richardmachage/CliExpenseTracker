package dev.forsythe.data

import dev.forsythe.data.model.Expense
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Thread-safe in-memory storage for expenses.
 * Uses a Mutex to ensure atomic updates to the state, preventing race conditions
 * during concurrent 'add' or 'delete' operations.
 */
object LocalDb {

    private val mutex = Mutex()

    val listOfExpenses : MutableList<Expense> = mutableListOf()

    suspend fun <T> safeDbOperation(block: suspend () -> T) : T = mutex.withLock {
            block()
        }

}

