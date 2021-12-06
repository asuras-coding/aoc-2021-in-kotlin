fun main() {
    fun part1(input: List<String>): Int {
        var lanternFishes = input.first().split(",").map { Lanternfish(it.toInt()) }
        repeat(80) {
            lanternFishes = passTime(lanternFishes)
        }
        return lanternFishes.size
    }

    fun part2(input: List<String>, days: Int = 256): Long {
        val lanternFishes = input.first().split(",").map { Lanternfish(it.toInt()) }
        val fishMap = IntRange(0, 8).map { LanternfishGroup(it, lanternFishes.filter { f -> f.timer == it }.size.toLong()) }
        repeat(days) {
            passTime2(fishMap)
        }
        return fishMap.sumOf { it.number }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 5934)
    check(part2(testInput) == 26984457539L)

    val input = readInput("Day06")
    println(part1(input))
    println(part2(input))
}

@JvmInline
value class Lanternfish(val timer: Int = 8)

private fun passTime(fishes: List<Lanternfish>): List<Lanternfish> {
    val newFishes = mutableListOf<Lanternfish>()
    return fishes.map {
        if (it.timer == 0) {
            newFishes.add(Lanternfish())
            Lanternfish(6)
        } else {
            Lanternfish(it.timer - 1)
        }
    }.plus(newFishes)
}

data class LanternfishGroup(var timer: Int, var number: Long)

private fun passTime2(fishes: List<LanternfishGroup>) {
    fishes.forEach { it.timer = it.timer - 1 }
    val minOneFishes = fishes.first { it.timer == -1 }
    minOneFishes.timer = 8
    val sixFishes = fishes.first { it.timer == 6 }
    sixFishes.number += minOneFishes.number
}
