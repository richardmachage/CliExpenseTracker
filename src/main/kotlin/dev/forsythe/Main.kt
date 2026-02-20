package dev.forsythe

import dev.forsythe.cli.CliParser
import dev.forsythe.cli.CommandDispatcher
import dev.forsythe.domain.AddExpenseUseCase
import dev.forsythe.domain.DeleteExpenseUseCase
import dev.forsythe.domain.ListExpensesUseCase
import dev.forsythe.domain.SummaryUseCase
import kotlinx.coroutines.runBlocking


fun main() = runBlocking{


    val dispatcher = CommandDispatcher(
        addExpenseUseCase = AddExpenseUseCase(),
        listExpensesUseCase = ListExpensesUseCase(),
        deleteExpenseUseCase = DeleteExpenseUseCase(),
        summaryUseCase = SummaryUseCase()
    )

    println("Expense Tracker CLI Initialized. Type 'exit' to quit.")


    while (true) {
        print(" > ")

        val input = readlnOrNull() ?: break

        val trimmed = input.trim()

        //exit command
        if (trimmed.equals("exit", ignoreCase = true)){
            println("Goodbye!")
            break
        }

        if (trimmed.isEmpty()) continue


        //execution pipeline connection
        // The raw string goes in, the parsed object comes out, and the dispatcher routes it.
        val command = CliParser.parse(trimmed)
        dispatcher.dispatch(command)
    }
}


