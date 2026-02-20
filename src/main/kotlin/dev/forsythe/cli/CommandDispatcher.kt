package dev.forsythe.cli

import dev.forsythe.domain.AddExpenseUseCase
import dev.forsythe.domain.DeleteExpenseUseCase
import dev.forsythe.domain.ListExpensesUseCase
import dev.forsythe.domain.SummaryUseCase
import dev.forsythe.utils.toFormattedDate
import kotlin.fold


/**
 * This defines the flow between the parsed CLI commands and the Domain Layer. Simply acts as the interface bridge
 */
class CommandDispatcher(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val listExpensesUseCase: ListExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val summaryUseCase: SummaryUseCase
) {

    suspend fun dispatch(command: CliCommand) {
        when(command){
            is CliCommand.AddExpense -> {
                addExpenseUseCase(
                    amount = command.amount,
                    category = command.category,
                    description = command.description,
                    date = command.date
                ).fold(
                        onSuccess = {expense ->
                            println("Added expense #${expense.id}: ${expense.amount} | ${expense.category} | ${expense.description ?: ""} | ${expense.date.toFormattedDate()}")
                        },
                        onFailure = {
                            println("Error adding expense: ${it.message}")
                        }
                    )
            }
            is CliCommand.Delete     -> {
                deleteExpenseUseCase(command.id)
                    .fold(
                        onSuccess = {
                            println("Expense ID ${command.id} deleted successfully")
                        },
                        onFailure = {
                            println("Error deleting expense: ${it.message}")
                        }
                    )
            }
            CliCommand.Summary    -> {
                summaryUseCase()
                    .fold(
                        onSuccess = { summary ->
                            print("Total Spent: ${summary.total} ")
                            summary.categoryTotals.forEach { (category, catTotal) ->
                                print(" $category : $catTotal ")
                            }
                            println("")
                        },
                        onFailure = { error ->
                            println(error.message)
                        }
                    )
            }
            is CliCommand.List    -> {
                listExpensesUseCase(
                    limit = command.limit,
                    category = command.category
                ).fold(
                    onSuccess = {allExpenses ->
                        if (allExpenses.isEmpty()) {
                            println("No expenses found.")
                            return@fold
                        }else {
                            allExpenses.forEach { expense ->
                                println(
                                    "#${expense.id}: ${expense.amount} | ${expense.category} | ${expense.description ?: ""} | ${expense.date.toFormattedDate()}"
                                )
                            }
                        }
                    },
                    onFailure = {
                        println("Error listing expenses: ${it.localizedMessage}")
                    }
                )
            }
            is CliCommand.Unknown -> println("Unknown command.Type exit to quit.")


            is CliCommand.Invalid -> println(command.message)

        }
    }
}