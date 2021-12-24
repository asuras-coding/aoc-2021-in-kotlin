// Implemented semi-manual check for analysis by Stefansfrank (https://github.com/Stefansfrank/advent_of_code_2021/blob/main/d24.solution.txt)
fun main() {
    val input = readInput("Day24")

    // These are the parameters for my input
    // For every input
    // P1 = when to div by 1 or 26
    // P2 = what to add to X
    // P3 = what to add to y
    val p1 = "1   1   1   1   1  26   1  26  26   1  26  26  26  26".replace("""\s+""".toRegex(), " ").split(" ").map { it.toInt() }
    val p2 = "10  12  10  12  11 -16  10 -11 -13  13  -8  -1  -4 -14".replace("""\s+""".toRegex(), " ").split(" ").map { it.toInt() }
    val p3 = "12   7   8   8  15  12   8  13   3  13   3   9   4  13".replace("""\s+""".toRegex(), " ").split(" ").map { it.toInt() }

    // when I made up pairs containing of the first p1 == 26 and the closest (unused) p1 == 1 to the left
    // search could be automated, and also p1 to p3 could be read automatically
    val pairs = listOf(4 to 5, 6 to 7, 3 to 8, 9 to 10, 2 to 11, 1 to 12, 0 to 13)
    val digits = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)

    val max = mutableListOf<Pair<Int, Int>>()
    val min = mutableListOf<Pair<Int, Int>>()

    // For every detected pair find possible inputs and add to inputPairs
    for (pair in pairs) {
        val inputPairs = mutableListOf<Pair<Int, Int>>()
        for (a in digits) for (b in digits) {
            if (b == a + p3[pair.first] + p2[pair.second]) inputPairs.add(a to b)
        }
        // Get max and mins
        val localMax = inputPairs.maxByOrNull { it.first * 10 + it.second }!!
        max.addAll(listOf(pair.first to localMax.first, pair.second to localMax.second))
        val localMin = inputPairs.minByOrNull { it.first * 10 + it.second }!!
        min.addAll(listOf(pair.first to localMin.first, pair.second to localMin.second))
    }
    // map input pairs to actual numbers and check if the program returns valid for them
    val maxNumber = max.sortedBy { it.first }.joinToString("") { "${it.second}" }
    val minNumber = min.sortedBy { it.first }.joinToString("") { "${it.second}" }
    println("max: $maxNumber is ${evaluateProgramForInput(input, maxNumber)}")
    println("min: $minNumber is ${evaluateProgramForInput(input, maxNumber)}")

}

// -------------------- random alu logic stuff -------------------------

private fun evaluateProgramForInput(program: List<String>, input: String): Boolean {
    resetVariables()
    index = 0
    program.forEach { runCommand(it, input) }
    return Z.value == 0
}

private var index = 0

private fun runCommand(command: String, input: String) {
    val args = command.split(" ")
    when (args.first()) {
        "inp" -> inp(args[1], input)
        "add" -> add(args[1], args[2])
        "mul" -> mul(args[1], args[2])
        "div" -> div(args[1], args[2])
        "mod" -> mod(args[1], args[2])
        "eql" -> eql(args[1], args[2])
    }
}

private fun String.toVariable(): Variable = when (this) {
    "w" -> W
    "x" -> X
    "y" -> Y
    "z" -> Z
    else -> error("$this not a variable")
}

private fun String.isVariable(): Boolean = this in listOf("w", "x", "y", "z")

private fun resetVariables() {
    mul(X, 0)
    mul(X, 0)
    mul(Y, 0)
    mul(Z, 0)
}

private fun inp(a: String, input: String) {
    inp(a.toVariable(), input[index++].toString().toInt())
}

private fun inp(a: Variable, input: Int) {
    a.value = input
}

private fun add(a: String, b: String) {
    if (b.isVariable()) add(a.toVariable(), b.toVariable())
    else add(a.toVariable(), b.toInt())
}

private fun add(a: Variable, b: Variable) {
    a.value += b.value
}

private fun add(a: Variable, b: Int) {
    a.value += b
}

private fun mul(a: String, b: String) {
    if (b.isVariable()) mul(a.toVariable(), b.toVariable())
    else mul(a.toVariable(), b.toInt())
}

private fun mul(a: Variable, b: Variable) {
    a.value *= b.value
}

private fun mul(a: Variable, b: Int) {
    a.value *= b
}

private fun div(a: String, b: String) {
    if (b.isVariable()) div(a.toVariable(), b.toVariable())
    else div(a.toVariable(), b.toInt())
}

// never b=0
private fun div(a: Variable, b: Variable) {
    a.value /= b.value
}

private fun div(a: Variable, b: Int) {
    a.value /= b
}

private fun mod(a: String, b: String) {
    if (b.isVariable()) mod(a.toVariable(), b.toVariable())
    else mod(a.toVariable(), b.toInt())
}

// never a<0 or b<=0
private fun mod(a: Variable, b: Variable) {
    a.value %= b.value
}

private fun mod(a: Variable, b: Int) {
    a.value %= b
}

private fun eql(a: String, b: String) {
    if (b.isVariable()) eql(a.toVariable(), b.toVariable())
    else eql(a.toVariable(), b.toInt())
}

private fun eql(a: Variable, b: Variable) {
    a.value = if (a.value == b.value) 1 else 0
}

private fun eql(a: Variable, b: Int) {
    a.value = if (a.value == b) 1 else 0
}

sealed class Variable {
    var value: Int = 0
}

object W : Variable()
object X : Variable()
object Y : Variable()
object Z : Variable()