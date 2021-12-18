fun main() {
    fun part1(input: List<String>): Long {
        val sum = input.reduce { acc, s -> add(acc, s) }
        return magnitude(sum)
    }

    fun part2(input: List<String>): Long {
        // luckily x + x seems to not produce the max magnitude
        return cartesianProduct(input, input).maxOf { magnitude(add(it.first,it.second)) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140L)

    val input = readInput("Day18")
    println(part1(input))
    check(part2(testInput) == 3993L)
    println(part2(input))
}

private fun <T, U> cartesianProduct(c1: Collection<T>, c2: Collection<U>): List<Pair<T, U>> {
    return c1.flatMap { lhsElem -> c2.map { rhsElem -> lhsElem to rhsElem } }
}

private fun add(a: String, b: String): String {
    return reduce("[$a,$b]")
}

val pairRegex = """\[(\d+),(\d+)\]""".toRegex()

private fun reduce(snailfish: String): String {
    var currentFish = snailfish
    var success = true
    while (success) {
        while (success) {
            val res = explodeIter(currentFish)
            success = res.first
            currentFish = res.second
        }
        val res = splitIter(currentFish)
        success = res.first
        currentFish = res.second
    }
    return currentFish
}

private fun explodeIter(fish: String): Pair<Boolean, String> {
    var index = 0
    var depth = 0
    while (index < fish.lastIndex) {
        when {
            fish[index] == '[' -> {
                depth++
                val nextPairCloseIndex = fish.substring(index).indexOfFirst { it == ']' } + index + 1
                if (depth > 4 && fish.substring(index, nextPairCloseIndex).matches(pairRegex)) {
                    return true to explode(fish, index, nextPairCloseIndex)
                }
            }
            fish[index] == ']' -> depth--
        }
        index++
    }
    return false to fish
}

private fun splitIter(fish: String): Pair<Boolean, String> {
    var index = 0
    while (index < fish.lastIndex) {
        when {
            fish[index].isDigit() && fish[index + 1].isDigit() -> {
                return true to  split(fish, index)
            }
        }
        index++
    }
    return false to fish
}

private fun magnitude(snailfish: String): Long {
    var currentFish = snailfish
    var index = 0
    while (index < currentFish.lastIndex) {
        if (currentFish[index] == '[') {
            val nextPairCloseIndex = currentFish.substring(index).indexOfFirst { it == ']' } + index + 1
            if (currentFish.substring(index, nextPairCloseIndex).matches(pairRegex)) {
                currentFish = replaceMagnitude(currentFish, index, nextPairCloseIndex)
                index = 0
                continue
            }
        }
        index++
    }
    return currentFish.toLong()
}

private fun replaceMagnitude(snailfish: String, startIndex: Int, endIndex: Int): String {
    val (a, b) = pairRegex.matchEntire(snailfish.substring(startIndex, endIndex))!!.destructured
    return snailfish.replaceRange(startIndex, endIndex, "${a.toLong() * 3 + b.toLong() * 2}")
}

private fun split(snailfish: String, index: Int): String {
    var i = index
    while (snailfish[i].isDigit()) {
        i++
    }
    val endIndex = i
    val replaceRange = snailfish.replaceRange(index, endIndex, splitToPair(snailfish.substring(index, endIndex)))
    return replaceRange
}

private fun splitToPair(string: String): String {
    val number = string.toInt()
    return "[${number / 2},${number - (number / 2)}]"
}

private fun explode(snailfish: String, startIndex: Int, endIndex: Int): String {
    val (a, b) = pairRegex.matchEntire(snailfish.substring(startIndex, endIndex))!!.destructured
    var intermediateFish = snailfish.replaceRange(startIndex, endIndex, "0")
    intermediateFish = replaceLeft(a, startIndex, intermediateFish)
    intermediateFish = replaceRight(b, startIndex + 1, intermediateFish)
    return intermediateFish
}

private fun replaceLeft(number: String, index: Int, snailfish: String): String {
    var i = index - 1
    while (!snailfish[i].isDigit()) {
        i--
        if (i < 0) return snailfish
    }
    val endIndex = i
    while (snailfish[i].isDigit()) {
        i--
    }
    val startIndex = i
    return snailfish.replaceRange(
        startIndex + 1,
        endIndex + 1,
        "${(snailfish.substring(startIndex + 1, endIndex + 1).toInt() + number.toInt())}"
    )
}

private fun replaceRight(number: String, index: Int, snailfish: String): String {
    var i = index + 1
    while (!snailfish[i].isDigit()) {
        i++
        if (i > snailfish.lastIndex) return snailfish
    }
    val startIndex = i
    while (snailfish[i].isDigit()) {
        i++
    }
    val endIndex = i
    return snailfish.replaceRange(startIndex, endIndex, "${snailfish.substring(startIndex, endIndex).toInt() + number.toInt()}")
}