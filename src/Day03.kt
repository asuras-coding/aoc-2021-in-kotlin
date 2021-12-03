fun main() {
    fun part1(input: List<String>): Int {
        val length = input.first().length
        val countArray = IntArray(length) { 0 }
        input.forEach { line ->
            line.forEachIndexed { index, c ->
                countArray[index] = countArray[index] + if (c == '1') 1 else 0
            }
        }
        val gammaRate = countArray.map { if (it >= input.size / 2) 1 else 0 }.joinToString("")
        val epsilonRate = gammaRate.map { if (it == '1') 0 else 1 }.joinToString("")
        return multipleBinaryStrings(gammaRate, epsilonRate)
    }

    fun part2(input: List<String>): Int {

        var oxy = input
        for (i in 0..input.first().lastIndex) {
            if (oxy.size <= 1) break
            oxy = filterOxy(oxy.groupBy { it[i] })
        }
        var co2 = input
        for (i in 0..input.first().lastIndex) {
            if (co2.size <= 1) break
            co2 = filterCo2(co2.groupBy { it[i] })
        }
        return multipleBinaryStrings(oxy.first(), co2.first())
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}

private fun filterOxy(group: Map<Char, List<String>>): List<String> {
    val grp1 = group.getOrDefault('1', emptyList())
    val grp0 = group.getOrDefault('0', emptyList())
    return if (grp1.size >= grp0.size) grp1 else grp0
}

private fun filterCo2(group: Map<Char, List<String>>): List<String> {
    val grp0 = group.getOrDefault('0', emptyList())
    val grp1 = group.getOrDefault('1', emptyList())
    return if (grp0.size <= grp1.size) grp0 else grp1
}

private fun multipleBinaryStrings(a: String, b: String): Int =
    (a.toInt(2) * b.toInt(2))