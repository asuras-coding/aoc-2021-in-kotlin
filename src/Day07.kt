import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        val minOf = (positions.minOrNull()!!..positions.maxOrNull()!!).minOf {
            val sum = positions.sumOf { p -> abs(it - p) }
            sum
        }
        return minOf
    }

    fun part2(input: List<String>): Int {
        val positions = input.first().split(",").map { it.toInt() }
        val minOf = (positions.minOrNull()!!..positions.maxOrNull()!!).minOf {
            val sum = positions.sumOf { p ->
                val n = abs(it - p)
                (n * (n + 1)) / 2
            }
            sum
        }
        return minOf
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
