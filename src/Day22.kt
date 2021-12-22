import kotlin.math.abs

fun main() {

    fun part1(input: List<String>): Long {
        val rebootSteps = readSteps(input).take(20)
        val turnedOnCubes = mutableSetOf<Cube2>()
        for ((i, step) in rebootSteps.withIndex()) {
            for (x in step.cube.x) {
                for (y in step.cube.y) {
                    for (z in step.cube.z) {
                        if (step is On) {
                            turnedOnCubes.add(Cube2(x, y, z))
                        } else {
                            turnedOnCubes.remove(Cube2(x, y, z))
                        }
                    }
                }
            }
            println("size after step: ${i+1} = ${turnedOnCubes.size}")
        }
        return turnedOnCubes.size.toLong()
    }

    fun part2(input: List<String>): Long {
        val steps = readSteps(input)
        return executeSteps(steps)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    val testInput2 = readInput("Day22_testb")
    check(part1(testInput) == 590784L)
    check(part2(testInput2) == 2758514936282235L)
    // 2758514936282235 <- solution
    // 2758514936265179 <- mine

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}

private fun readSteps(input: List<String>): List<Step> = input.map(::toStep)

private fun executeSteps(steps: List<Step>): Long {
    var cubeRanges = emptyList<Cube>()
    steps.forEachIndexed { i, step ->
        cubeRanges = cubeRanges.flatMap { it.cutOut(step.cube) }
        if (step is On) cubeRanges = cubeRanges + step.cube
        println("size after step: ${i + 1} = ${cubeRanges.sumOf { it.size }}")
    }
    return cubeRanges.sumOf { it.size }
}

private fun toStep(line: String): Step {
    val (operator, ranges) = line.split(" ")
    val (x, y, z) = ranges.split(",")
    val (x1, x2) = x.split("=").last().split("..").map { it.toInt() }
    val (y1, y2) = y.split("=").last().split("..").map { it.toInt() }
    val (z1, z2) = z.split("=").last().split("..").map { it.toInt() }
    return when (operator) {
        "on" -> On(Cube(x1..x2, y1..y2, z1..z2))
        else -> Off(Cube(x1..x2, y1..y2, z1..z2))
    }
}

fun IntRange.size() = if (isEmpty()) 0 else abs(last - first) + 1

private fun IntRange.limitToRange(other: IntRange): IntRange =
    IntRange(this.first.coerceAtLeast(other.first), this.last.coerceAtMost(other.last))

private infix fun Int.exclusiveTo(end: Int) = IntRange(this + 1, end - 1)
private infix fun Int.exclusiveStart(end: Int) = IntRange(this + 1, end)
private infix fun Int.exclusiveEnd(end: Int) = IntRange(this, end - 1)

sealed class Step(open val cube: Cube)
data class On(override val cube: Cube) : Step(cube)
data class Off(override val cube: Cube) : Step(cube)

data class Cube(val x: IntRange, val y: IntRange, val z: IntRange) {
    val size: Long
        get() = x.size().toLong() * y.size().toLong() * z.size().toLong()

    private fun doesNotIntersect(cube: Cube): Boolean {
        return (x.last < cube.x.first || x.first > cube.x.last ||
                y.last < cube.y.first || y.first > cube.y.last ||
                z.last < cube.z.first || z.first > cube.z.last)
    }

    private fun isIncludedIn(cube: Cube): Boolean {
        return (x.first >= cube.x.first && x.last <= cube.x.last) &&
                (y.first >= cube.y.first && y.last <= cube.y.last) &&
                (z.first >= cube.z.first && z.last <= cube.z.last)
    }

    fun cutOut(cut: Cube): List<Cube> {
        if (doesNotIntersect(cut)) return listOf(this)
        if (isIncludedIn(cut)) return emptyList()
        val cuts = mutableListOf<Cube>()
        if (canCutLeft(cut.x)) cuts.add(cutLeft(cut.x))
        if (canCutRight(cut.x)) cuts.add(cutRight(cut.x))
        if (canCutTop(cut.y)) cuts.add(cutTop(cut.x, cut.y))
        if (canCutBottom(cut.y)) cuts.add(cutBottom(cut.x, cut.y))
        if (canCutFront(cut.z)) cuts.add(cutFront(cut.x, cut.y, cut.z))
        if (canCutBack(cut.z)) cuts.add(cutBack(cut.x, cut.y, cut.z))
        return cuts.toList()
    }

    private fun canCutLeft(xRange: IntRange): Boolean = xRange.first in x.first exclusiveTo x.last
    private fun canCutRight(xRange: IntRange): Boolean = xRange.last in x.first exclusiveTo x.last
    private fun canCutBottom(yRange: IntRange): Boolean = yRange.first in y.first exclusiveTo y.last
    private fun canCutTop(yRange: IntRange): Boolean = yRange.last in y.first exclusiveTo y.last
    private fun canCutFront(zRange: IntRange): Boolean = zRange.first in z.first exclusiveTo z.last
    private fun canCutBack(zRange: IntRange): Boolean = zRange.last in z.first exclusiveTo z.last

    private fun cutLeft(xRange: IntRange): Cube =
        Cube(x.first exclusiveEnd xRange.first, y, z)

    private fun cutRight(xRange: IntRange): Cube =
        Cube(xRange.last exclusiveStart x.last, y, z)

    private fun cutBottom(xRange: IntRange, yRange: IntRange): Cube =
        Cube(xRange.limitToRange(x), y.first exclusiveEnd yRange.first, z)

    private fun cutTop(xRange: IntRange, yRange: IntRange): Cube =
        Cube(xRange.limitToRange(x), yRange.last exclusiveStart y.last, z)

    private fun cutFront(xRange: IntRange, yRange: IntRange, zRange: IntRange): Cube =
        Cube(xRange.limitToRange(x), yRange.limitToRange(y), z.first exclusiveEnd zRange.first)

    private fun cutBack(xRange: IntRange, yRange: IntRange, zRange: IntRange): Cube =
        Cube(xRange.limitToRange(x), yRange.limitToRange(y), zRange.last exclusiveStart z.last)
}
