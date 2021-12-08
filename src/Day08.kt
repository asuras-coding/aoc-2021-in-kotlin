fun main() {
    fun part1(input: List<String>): Int {
        val signals = input.map {
            val (pattern, signal) = it.split(" | ")
            Signal(pattern.split(" "), signal.split(" "))
        }
        return signals.sumOf { it.signal.count { s -> s.length in listOf(2, 4, 3, 7) } }
    }

    fun part2(input: List<String>): Int {
        val signals = input.map {
            val (pattern, signal) = it.split(" | ")
            Signal(
                pattern.split(" ").map { s -> s.toSortedSet().joinToString("") },
                signal.split(" ").map { s -> s.toSortedSet().joinToString("") })
        }
        return signals.sumOf { it.decode() }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}

data class Signal(val signalPatterns: List<String>, val signal: List<String>) {
    fun decode(): Int {
        val signalMap = signalPatterns.associateWith { -1 }.toMutableMap()
        signalMap[signalPatterns.first { it.length == 2 }] = 1
        signalMap[signalPatterns.first { it.length == 4 }] = 4
        signalMap[signalPatterns.first { it.length == 3 }] = 7
        signalMap[signalPatterns.first { it.length == 7 }] = 8
        signalMap[decodeThree(signalMap)] = 3
        signalMap[decodeSix(signalMap)] = 6
        signalMap[decodeFive(signalMap)] = 5
        signalMap[decodeTwo(signalMap)] = 2
        signalMap[decodeNine(signalMap)] = 9
        signalMap[decodeZero(signalMap)] = 0
        return signal.map { signalMap[it] }.joinToString("").toInt()
    }
}

private fun decodeThree(signalMap: Map<String, Int>): String {
    return signalMap.keys.first { it.length == 5 && signalMap.findNumber(7).all { s -> s in it } }
}

private fun decodeSix(signalMap: Map<String, Int>): String {
    return signalMap.keys.first { it.length == 6 && signalMap.findNumber(7).any { s -> s !in it } }
}

private fun decodeFive(signalMap: Map<String, Int>): String {
    return signalMap.keys.first { it.length == 5 && it.all { c -> c in signalMap.findNumber(6) } }
}

private fun decodeTwo(signalMap: Map<String, Int>): String {
    return signalMap.keys.first { it.length == 5 && it !in listOf(signalMap.findNumber(3), signalMap.findNumber(5)) }
}

private fun decodeNine(signalMap: Map<String, Int>): String {
    return signalMap.keys.first {
        it.length == 6 && signalMap.findNumber(5).all { s -> s in it } && signalMap.findNumber(7).all { s -> s in it }
    }
}

private fun decodeZero(signalMap: Map<String, Int>): String {
    return signalMap.keys.first { it.length == 6 && it !in listOf(signalMap.findNumber(6), signalMap.findNumber(9)) }
}

private fun Map<String, Int>.findNumber(number: Int): String {
    return this.entries.first { it.value == number }.key
}
