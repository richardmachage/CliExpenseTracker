import dev.forsythe.cli.CliCommand
import dev.forsythe.cli.CliParser
import dev.forsythe.utils.toInstantFromDate
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CliParserTest {

    @Test
    fun `core engine handles empty and unknown inputs`(){
        assertEquals(CliCommand.Unknown, CliParser.parse(""))
        assertEquals(CliCommand.Unknown, CliParser.parse("   "))
        assertEquals(CliCommand.Unknown, CliParser.parse("update 5"))
        assertEquals(CliCommand.Unknown, CliParser.parse("gibberish"))
    }

    @Test
    fun `parser is case insensitive for command names`(){
        val result = CliParser.parse("Add 100 Food")
        assertIs<CliCommand.AddExpense>(result)
        assertEquals(BigDecimal("100"), result.amount)
        assertEquals("Food", result.category)
    }


    @Test
    fun `tokenization correctly extracts quoted strings`() {
        val result = CliParser.parse("add 50.50 \"Fast Food\" \"Burger King\"")
        assertIs<CliCommand.AddExpense>(result)
        assertEquals(BigDecimal("50.50"), result.amount)
        assertEquals("Fast Food", result.category)
        assertEquals("Burger King", result.description)
    }

    @Test
    fun `add command parses correctly with minimum and maximum arguments`() {
        // Minimum args
        val minResult = CliParser.parse("add 150 Groceries")
        assertIs<CliCommand.AddExpense>(minResult)

        assertEquals(BigDecimal("150"), minResult.amount)
        assertEquals("Groceries", minResult.category)
        assertEquals(null, minResult.description)
        assertEquals(null, minResult.date)

        // Maximum args
        val expectedDate = "2026-02-20".toInstantFromDate()
        val maxResult = CliParser.parse("add 100 Food Lunch 2026-02-20")
        assertIs<CliCommand.AddExpense>(maxResult)
        assertEquals(BigDecimal("100"), maxResult.amount)
        assertEquals("Food", maxResult.category)
        assertEquals("Lunch", maxResult.description)
        assertEquals(expectedDate, maxResult.date)
    }

    @Test
    fun `add command returns Invalid for missing or malformed arguments`() {
        assertIs<CliCommand.Invalid>(CliParser.parse("add 100")) // Missing category
        assertIs<CliCommand.Invalid>(CliParser.parse("add ABC Food")) // Bad decimal
        assertIs<CliCommand.Invalid>(CliParser.parse("add 100 Food Lunch 20-02-2026")) // Bad date format
    }

    @Test
    fun `delete command parses correctly and catches errors`(){
        val result = CliParser.parse("delete 5")

        assertIs<CliCommand.Delete>(result)
        assertEquals(5, result.id)

        assertIs<CliCommand.Invalid>(CliParser.parse("delete")) // missing ID
        assertIs<CliCommand.Invalid>(CliParser.parse("delete five")) //non integer Id
    }

    @Test
    fun `list command handles polymorphic arguments accurately`(){
        // No args
        assertEquals(CliCommand.List(null, null), CliParser.parse("list"))

        // Limit only
        assertEquals(CliCommand.List(10, null), CliParser.parse("list 10"))

        // Category only
        assertEquals(CliCommand.List(null, "Groceries"), CliParser.parse("list Groceries"))

        // Both args
        assertEquals(CliCommand.List(10, "Groceries"), CliParser.parse("list 10 Groceries"))
    }

    @Test
    fun `list command returns Invalid for malformed arguments`() {
        assertIs<CliCommand.Invalid>(CliParser.parse("list Groceries 10")) // Swapped args
        assertIs<CliCommand.Invalid>(CliParser.parse("list 10 Groceries Extra")) // Too many args
    }


    @Test
    fun `summary command parses correctly and strictly rejects arguments`() {
        assertEquals(CliCommand.Summary, CliParser.parse("summary"))
        assertIs<CliCommand.Invalid>(CliParser.parse("summary 2026")) // Should not accept args
    }
}