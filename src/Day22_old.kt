import kotlin.math.abs

fun main() {

    fun part1(input: List<String>): Int {
        val rebootSteps = readSteps(input).take(20)
        val turnedOnCubes = mutableSetOf<Cube2>()
        var i = 0
        for (step in rebootSteps) {
            for (x in step.cubeRange.x) {
                for (y in step.cubeRange.y) {
                    for (z in step.cubeRange.z) {
                        if (step.on) {
                            turnedOnCubes.add(Cube2(x, y, z))
                        } else {
                            turnedOnCubes.remove(Cube2(x, y, z))
                        }
                    }
                }
            }
            println("size after step: ${i+1} = ${turnedOnCubes.size}")
            i++
        }
        return turnedOnCubes.size
    }

    fun part2(input: List<String>): Long {
        println("------")
        val rebootSteps = readSteps(input)
        var turnedOnCubeRanges = emptyList<CubeRange>()
        rebootSteps.forEach { rebootStep ->
            turnedOnCubeRanges = turnedOnCubeRanges.flatMap { it.splitCubeRangeAround(rebootStep.cubeRange) }
            if (rebootStep.on) turnedOnCubeRanges = turnedOnCubeRanges + rebootStep.cubeRange
            println(turnedOnCubeRanges.sumOf { it.size })
        }
        return turnedOnCubeRanges.sumOf { it.size }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day22_test")
    //println(part1(testInput))
    //println(part2(testInput))
    check(part1(testInput) == 590784)
    check(part2(testInput) == 2758514936282235L)

    // 21049844681660
    // 2758514936282235
    val input = readInput("Day22")
    //println(part1(input))
    //println(part2(input))
}


private fun readSteps(input: List<String>): List<RebootStep> {
    return input.map(::toRebootStep)
}

private fun toRebootStep(line: String): RebootStep {
    val (operator, ranges) = line.split(" ")
    val (x, y, z) = ranges.split(",")
    val (x1, x2) = x.split("=").last().split("..").map { it.toInt() }
    val (y1, y2) = y.split("=").last().split("..").map { it.toInt() }
    val (z1, z2) = z.split("=").last().split("..").map { it.toInt() }
    return RebootStep(CubeRange(x1..x2, y1..y2, z1..z2), operator == "on")
}

data class Cube2(val x: Int, val y: Int, val z: Int)

data class RebootStep(val cubeRange: CubeRange, val on: Boolean = false)

private fun LongRange.size() = if (isEmpty()) 0 else (abs(last - first) + 1)

data class CubeRange(val x: IntRange, val y: IntRange, val z: IntRange) {

    val size: Long
        get() = x.size().toLong() * y.size().toLong() * z.size().toLong()

    fun isEmpty(): Boolean {
        return x.isEmpty() || y.isEmpty() || z.isEmpty()
    }

    fun splitCubeRangeAround(splitCube: CubeRange): List<CubeRange> {
        return listOfNotNull(
            cubesBelow(splitCube),
            cubesAbove(splitCube),
            cubesLeftOf(splitCube),
            cubesRightOf(splitCube),
            cubesInFrontOf(splitCube),
            cubesBehind(splitCube),
        ).filterNot { it.isEmpty() }
    }

    private fun cubesBelow(other: CubeRange): CubeRange? {
        return if (other.y.first - 1 in y) {
            CubeRange(x, y.first..other.y.first - 1, z)
        } else null
    }

    private fun cubesAbove(other: CubeRange): CubeRange? {
        return if (other.y.last + 1 in y) {
            CubeRange(x, other.y.last + 1..y.last, z)
        } else null
    }

    private fun cubesLeftOf(other: CubeRange): CubeRange? {
        return if (other.x.first - 1 in x) {
            CubeRange(x.first..other.x.first - 1, other.y.limitToRange(y), z)
        } else null
    }

    private fun cubesRightOf(other: CubeRange): CubeRange? {
        return if (other.x.last + 1 in x) {
            CubeRange(other.x.last + 1..x.last, other.y.limitToRange(y), z)
        } else null
    }

    private fun cubesInFrontOf(removeCubeRange: CubeRange): CubeRange? {
        return if (removeCubeRange.z.first - 1 in z) {
            CubeRange(removeCubeRange.x.limitToRange(x), removeCubeRange.y.limitToRange(y), z.first..removeCubeRange.z.first - 1)
        } else null
    }

    private fun cubesBehind(removeCubeRange: CubeRange): CubeRange? {
        return if (removeCubeRange.z.last + 1 in z) {
            CubeRange(removeCubeRange.x.limitToRange(x), removeCubeRange.y.limitToRange(y), removeCubeRange.z.last + 1..z.last)
        } else null
    }
}

private fun IntRange.limitToRange(other: IntRange): IntRange = this.first.coerceAtLeast(other.first)..this.last.coerceAtMost(other.last)

// calculate turned on interval so that all intervals are disjunct
// when turning off cubes, recalculate intervals to that they dont contain turned off cubes again
// 1..3,1..3,1..3 are turned on, 3..4,3..4,3..4 will be turned off -> 1..2,1..3,1..3;1..3;3,1..2,1..2 are still on
