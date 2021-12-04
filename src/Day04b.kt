fun main() {
    fun part1(input: List<String>): Int {
        val (drawNumbers, boards) = inputToDrawNumbersAndBoards(input)
        drawNumbers.forEach { number ->
            boards.forEach {
                it.drawNumber(number)
                if (it.isDone) return it.score
            }
        }
        return -1
    }

    fun part2(input: List<String>): Int {
        val (drawNumbers, boards) = inputToDrawNumbersAndBoards(input)
        drawNumbers.forEach { number ->
            boards.forEach {
                it.drawNumber(number)
            }
        }
        return boards.maxByOrNull { it.wonAtTurn }?.score ?: -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

data class Cell(val value: Int, var checked: Boolean = false)

data class Row(val values: List<Cell>)
data class Column(val values: List<Cell>)

fun List<Row>.toColumns(): List<Column> {
    return this.mapIndexed { index, _ -> Column(this.map { it.values[index] }) }
}

data class HistoryEntry(val turn: Int, val number: Int, val cell: Cell)

class CellBingoBoard(fields: List<List<Int>>) {
    private val rows = fields.map { r -> Row(r.map { Cell(it) }) }
    private val columns = rows.toColumns()
    private val cells = rows.flatMap { it.values }
    private val history = mutableListOf<HistoryEntry>()
    private var turn = 0

    val isDone: Boolean
        get() = rows.any { r -> r.values.all { it.checked } } || columns.any { c -> c.values.all { it.checked } }

    val score: Int
        get() = if (!isDone) -1 else cells.filter { !it.checked }.sumOf { it.value } * history.last().number

    val wonAtTurn: Int
        get() = if (!isDone) -1 else turn

    fun drawNumber(number: Int) {
        if (isDone) return
        turn++
        cells.forEach {
            if (it.value == number) {
                it.checked = true
                history.add(HistoryEntry(turn, number, it))
            }
        }
    }
}

private fun inputToDrawNumbersAndBoards(input: List<String>): Pair<List<Int>, List<CellBingoBoard>> {
    val drawNumbers = input.first().split(",").map { it.toInt() }
    val boards =
        input.asSequence().drop(2).map { l -> l.split(" ").filter { it.isNotBlank() }.map { it.toInt() } }.filter { it.size > 1 }.chunked(5)
            .map {
                CellBingoBoard(it)
            }.toList()
    return drawNumbers to boards
}