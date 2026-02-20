package dev.forsythe.cli

import dev.forsythe.utils.toInstantFromDate
import java.math.BigDecimal
import java.time.Instant

object CliParser {
    fun parse(input: String): CliCommand {
        val trimmedInput = input.trim()
        if (trimmedInput.isBlank()) return CliCommand.Unknown

        // This regex matches either "quoted text" (Group 1) OR unquoted text (Group 2)
        val tokenRegex = Regex("\"([^\"]*)\"|(\\S+)")
        val parts = tokenRegex.findAll(trimmedInput).map { matchResult ->
            // If Group 1 is not empty, it was a quoted string (we extract it WITHOUT the quotes).
            // Otherwise, we take Group 2 (the normal word).
            matchResult.groupValues[1].ifBlank { matchResult.groupValues[2] }
        }.toList()

        val commandName  = parts.firstOrNull()?.lowercase() ?: return CliCommand.Unknown
        val args = parts.drop(1)

        val command = when (commandName) {
            "add" -> parseAdd(args)
            "delete" -> parseDelete(args)
            "list" -> parseList(args)
            "summary" -> parseSummary(args)
            else -> CliCommand.Unknown
        }

        return command
    }


    private fun parseAdd(args: List<String>): CliCommand {

        // Check for the absolute minimum required arguments
        if (args.size < 2) {
            return CliCommand.Invalid("Usage: add <amount> <category> [description] [date]")
        }

        val amountStr = args[0]
        val category = args[1]
        val description = args.getOrNull(2)
        val dateStr = args.getOrNull(3)

        //validate amount
        val amount = try {
            BigDecimal(amountStr)
        }
        catch (e: Exception) {
            return CliCommand.Invalid("Invalid amount: $amountStr\nUsage: add <amount> <category> [description] [date]")
        }

        // Validate the date if one was provided
        val date = dateStr?.let {
            try {
                it.toInstantFromDate()
                    ?: return CliCommand.Invalid(
                        "Invalid date format. Use yyyy-MM-dd \nUsage: add <amount> <category> [description] [date]"
                    )
            } catch (e: Exception) {
                return CliCommand.Invalid(
                    "Error: ${e.message}\nUsage: add <amount> <category> [description] [date]"
                )
            }
        }

        return CliCommand.AddExpense(amount, category, description, date)
    }

    private fun parseDelete(args: List<String>): CliCommand {
        if (args.isEmpty()) {
            return CliCommand.Invalid("Usage: delete <id>")
        }


        val idStr = args[0]
        val id = idStr.toIntOrNull()


        return id?.let {
            CliCommand.Delete(it)
        } ?: run {
            CliCommand.Invalid("Error: '$idStr' is not a valid ID.")
        }

    }

    private fun parseList(args: List<String>): CliCommand {
        if (args.size > 2) {
            return CliCommand.Invalid("Usage: list [limit] [category]")
        }

        var limit: Int? = null
        var category: String? = null

        if (args.size == 1) {
            val parsedInt = args[0].toIntOrNull()
            if (parsedInt != null) {
                limit = parsedInt
            }
            else {
                category = args[0]
            }
        }
        else if (args.size == 2) {
            val parsedInt = args[0].toIntOrNull()
            if (parsedInt != null) {
                limit = parsedInt
                category = args[1]
            }
            else {
                return CliCommand.Invalid("When providing two arguments, the first must be a number (limit).\nUsage: list [limit] [category]")
            }
        }
        return CliCommand.List(limit, category)
    }

    private fun parseSummary(args: List<String>): CliCommand {
        if (args.isNotEmpty()) return CliCommand.Invalid("Error: 'summary' does not accept any arguments.\nUsage: summary")

        return CliCommand.Summary
    }

}
