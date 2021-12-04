fun main() {
    fun part1(input: List<String>): Int {
        val (drawNumbers, boards) = inputToDrawNumbersAndBoards(input)
        drawNumbers.forEach { number ->
            boards.forEach {
                it.drawNumber(number)
                if (it.isDone()) return it.score()
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
        return boards.maxByOrNull { it.winRound() }?.score() ?: -1
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}

class BingoBoard(fields: List<List<Int>>) {
    private val board: Array<IntArray> = Array(5) { i -> IntArray(5) { j -> fields[i][j] } }
    private val history = mutableListOf<List<Int>>()
    private var turn = 0

    fun drawNumber(number: Int) {
        if (isDone()) return
        turn++
        for (i in 0..4) {
            for (j in 0..4) {
                if (board[i][j] == number) {
                    board[i][j] = -1
                    history.add(listOf(turn, number, i, j))
                }
            }
        }
    }

    fun isDone(): Boolean {
        for (i in 0..4) {
            if (board[i].all { it == -1 }) return true
        }
        for (j in 0..4) {
            if (board[0][j] == -1 && board[1][j] == -1 && board[2][j] == -1 && board[3][j] == -1 && board[4][j] == -1) return true
        }
        return false
    }

    fun score(): Int {
        if (!isDone()) return -1
        val sum = board.flatMap { it.filter { n -> n != -1 } }.sum()
        return sum * history.last()[1]
    }

    fun winRound(): Int {
        return if (isDone()) return history.last()[0] else -1
    }
}

private fun inputToDrawNumbersAndBoards(input: List<String>): Pair<List<Int>, List<BingoBoard>> {
    val drawNumbers = input.first().split(",").map { it.toInt() }
    val boards =
        input.asSequence().drop(2).map { l -> l.split(" ").filter { it.isNotBlank() }.map { it.toInt() } }.filter { it.size > 1 }.chunked(5)
            .map {
                BingoBoard(it) }.toList()
    return drawNumbers to boards
}