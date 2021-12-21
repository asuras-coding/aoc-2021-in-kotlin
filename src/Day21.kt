import kotlin.math.max

fun main() {
    fun part1(input: List<String>): Int {
        val diceBoard = setupBoard(input, 1000)
        val dic = Die()
        while (!diceBoard.isGameFinished) {
            diceBoard.turnPlayer(1, dic.roll3())
            if (diceBoard.isGameFinished) break
            diceBoard.turnPlayer(2, dic.roll3())
        }
        val loserScore = listOf(diceBoard.p1Score, diceBoard.p2Score).minOrNull() ?: 0
        return loserScore * dic.diceRolls
    }

    fun part2(input: List<String>): Long {
        val dice = QuantumDie()
        var quantumBoards = listOf(QuantumBoardHolder(1, setupBoard(input, 21)))
        var p1Wins = 0L
        var p2Wins = 0L
        while (quantumBoards.isNotEmpty()) {
            val (p1New, newBoards) = turnPlayer(quantumBoards, 1, dice)
            p1Wins += p1New
            quantumBoards = newBoards
            val (p2New, newBoards2) = turnPlayer(quantumBoards, 2, dice)
            p2Wins += p2New
            quantumBoards = newBoards2
        }
        return max(p1Wins, p2Wins)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day21_test")
    check(part1(testInput) == 739785)

    val input = readInput("Day21")
    println(part1(input))
    check(part2(testInput) == 444356092776315L)
    println(part2(input))
}

private fun setupBoard(input: List<String>, targetScore: Int): DiceBoard {
    val p1Pos = input.first().split(" ").last().toInt() - 1
    val p2Pos = input.last().split(" ").last().toInt() - 1
    return DiceBoard(p1Pos = p1Pos, p2Pos = p2Pos, targetScore = targetScore)
}

data class DiceBoard(var p1Pos: Int = 0, var p1Score: Int = 0, var p2Pos: Int = 0, var p2Score: Int = 0, val targetScore: Int = 1000) {
    fun turnPlayer(playerNumber: Int, diceResult: Int) {
        if (playerNumber == 1) {
            p1Pos = (p1Pos + diceResult) % 10
            p1Score += p1Pos + 1
        } else {
            p2Pos = (p2Pos + diceResult) % 10
            p2Score += p2Pos + 1
        }
    }

    val isGameFinished: Boolean
        get() = p1Score >= targetScore || p2Score >= targetScore

    fun getWinnerNumber() = if (p1Score > p2Score) 1 else 2
}

class Die {
    private val max: Int = 100
    var diceValue = 0
    var diceRolls = 0
    fun roll(): Int {
        diceRolls++
        diceValue++
        if (diceValue > max) diceValue = 1
        return diceValue
    }

    fun roll3(): Int = roll() + roll() + roll()
}

data class QuantumBoardHolder(var amount: Long = 1, val board: DiceBoard)

class QuantumDie() {
    enum class Values(val asInt: Int) {
        ONE(1),
        TWO(2),
        THREE(3)
    }

    fun roll(diceBoardList: List<Pair<DiceBoard, List<Int>>>): List<Pair<DiceBoard, List<Int>>> {
        return diceBoardList.flatMap { boardEntry ->
            Values.values().map { dice -> boardEntry.first.copy() to boardEntry.second.plus(dice.asInt) }
        }
    }

    fun roll3(diceBoardList: DiceBoard, playerNumber: Int): List<DiceBoard> {
        val quantumBoards: List<Pair<DiceBoard, List<Int>>> = roll(roll(roll(listOf(diceBoardList to emptyList()))))
        quantumBoards.forEach { board -> board.first.turnPlayer(playerNumber, board.second.sum()) }
        return quantumBoards.map { it.first }
    }
}

private fun turnPlayer(
    quantumBoardList: List<QuantumBoardHolder>,
    playerNumber: Int,
    dice: QuantumDie
): Pair<Long, List<QuantumBoardHolder>> {
    val boardVariations: List<Pair<DiceBoard, Long>> =
        quantumBoardList.flatMap { q -> dice.roll3(q.board, playerNumber).map { it to q.amount } }
    val quantumHolderVariations: List<QuantumBoardHolder> = boardVariations.groupBy { it.first }.map {
        QuantumBoardHolder(it.value.sumOf { b -> b.second }, it.key)
    }
    val playerWins =
        quantumHolderVariations.filter { it.board.isGameFinished && it.board.getWinnerNumber() == playerNumber }.sumOf { it.amount }
    return playerWins to quantumHolderVariations.filterNot { it.board.isGameFinished }
}