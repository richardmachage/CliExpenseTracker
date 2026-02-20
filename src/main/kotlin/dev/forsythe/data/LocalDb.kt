package dev.forsythe.data

import dev.forsythe.data.model.Expense
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object LocalDb {

    private val mutex = Mutex()

    val listOfExpenses : MutableList<Expense> = mutableListOf()

    suspend fun <T> safeDbOperation(block: suspend () -> T) : T = mutex.withLock {
            block()
        }

}

