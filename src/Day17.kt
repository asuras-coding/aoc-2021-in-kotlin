fun main() {
    fun part1(input: List<String>): Int {
        val targetArea = parseTargetArea(input.first())
        println(targetArea)
        val probes = mutableSetOf<Probe>()
        for (x in 0..targetArea.x.last) {
            for (y in targetArea.y.first..1000) {
                val probe = Probe(x, y)
                if (probe.moveUntilHitOrMiss(targetArea)) probes.add(probe)
                println("$x,$y,${probes.size}")
            }
        }
        return probes.maxOf { it.yMax }
    }

    fun part2(input: List<String>): Int {
        val targetArea = parseTargetArea(input.first())
        println(targetArea)
        val probes = mutableSetOf<Probe>()
        for (x in 0..targetArea.x.last) {
            for (y in targetArea.y.first..1000) {
                val probe = Probe(x, y)
                if (probe.moveUntilHitOrMiss(targetArea)) probes.add(probe)
                println("$x,$y,${probes.size}")
            }
        }
        println(probes.joinToString("  ") { "${it.v_xStart},${it.v_yStart}" })
        return probes.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}

data class TargetArea(val x: IntRange, val y: IntRange) {

}

data class Probe(var v_xStart: Int, var v_yStart: Int, var startX: Int = 0, var startY: Int = 0) {
    var x = startX
    var v_x = v_xStart
    var y = startY
    var v_y = v_yStart
    var yPosList = mutableListOf<Int>()
    val yMax
    get() = yPosList.maxOf { it }

    fun moveProbe() {
        x += v_x
        y += v_y
        yPosList.add(y)
        v_x = (v_x - 1).coerceAtLeast(0)
        v_y--
    }

    fun isInTargetArea(targetArea: TargetArea): Boolean {
        return x in targetArea.x && y in targetArea.y
    }

    fun moveUntilHitOrMiss(targetArea: TargetArea): Boolean {
        while(x <= targetArea.x.last && y >= targetArea.y.first && !isInTargetArea(targetArea)) {
            moveProbe()
        }
        return isInTargetArea(targetArea)
    }
}

// target area: x=20..30, y=-10..-5

fun parseTargetArea(input: String): TargetArea {
    val regex = """[\w\s]*x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)""".toRegex()
    val result = regex.find(input)
    val (x1, x2, y1, y2) = result!!.destructured
    return TargetArea(x1.toInt()..x2.toInt(), y1.toInt()..y2.toInt())
}

// travel x: p_x + x | x = x - 1
// travel y: p_y + y | y = y - 1