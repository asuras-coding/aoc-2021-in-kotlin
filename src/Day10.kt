fun main() {
    fun part1(input: List<String>): Int {
        return input.mapNotNull(::findFirstError).sumErrorScores()
    }

    fun part2(input: List<String>): Long {
        val incompleteLines = input.filter { findFirstError(it) == null }
        return incompleteLines.map(::findCompleteSequence).map(::calculateCompleteScore).sorted().median()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}

// --------- Part 1 ----------

val OPEN_CHAR_MAP = mapOf('(' to ')', '[' to ']', '<' to '>', '{' to '}')
val CLOSE_CHAR_MAP = mapOf(')' to '(', '[' to ']', '>' to '<', '}' to '{')
val PART_ONE_SCORE_MAP = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)

data class SubError(var position: Int, var char: Char, var expected: Char?)

fun findFirstError(line: String): SubError? {
    val stack = mutableListOf<Char>()
    line.forEachIndexed { index, c ->
        when (c) {
            in OPEN_CHAR_MAP.keys -> {
                stack.add(c)
            }
            OPEN_CHAR_MAP[stack.lastOrNull()] -> {
                stack.removeLast()
            }
            else -> return SubError(index, c, OPEN_CHAR_MAP[stack.lastOrNull()])
        }
    }
    return null
}

private fun List<SubError>.sumErrorScores(): Int =
    this.mapNotNull { PART_ONE_SCORE_MAP[it.char] }.sum()

// --------- Part 2 ----------

val PART_TWO_SCORE_MAP = mapOf(')' to 1, ']' to 2, '}' to 3, '>' to 4)

private fun findCompleteSequence(line: String): List<Char> {
    val stack = mutableListOf<Char>()
    val openingChars = listOf('(', '[', '<', '{')
    line.forEach { c ->
        when (c) {
            in openingChars -> {
                stack.add(c)
            }
            OPEN_CHAR_MAP[stack.lastOrNull()] -> {
                stack.removeLast()
            }
        }
    }
    return stack.reversed().mapNotNull { OPEN_CHAR_MAP[it] }
}

private fun calculateCompleteScore(list: List<Char>): Long =
    list.fold(0L) { acc, c ->
        acc * 5 + (PART_TWO_SCORE_MAP[c] ?: 0)
    }

private fun <T> List<T>.median(): T = this[this.lastIndex / 2]