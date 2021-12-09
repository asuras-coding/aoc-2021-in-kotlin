fun main() {
    fun part1(input: List<String>): Int {
        val heatMap = parseHeatMap(input)
        val lowPoints = findLowPoints(heatMap)
        return lowPoints.sumOf { l -> l.value + 1 }
    }

    fun part2(input: List<String>): Int {
        val heatMap = parseHeatMap(input)
        val lowPoints = findLowPoints(heatMap)
        return lowPoints.map { l -> findBasin(l, heatMap) }.sortedByDescending { b -> b.size }.take(3).multiply()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}


private fun parseHeatMap(input: List<String>): HeatMap {
    val list = input.map { it.chunked(1).map { a -> a.toInt() } }
    return HeatMap(list)
}

private fun List<Basin>.multiply(): Int {
    return this.map { b -> b.size }.fold(1) { a, b -> a * b }
}

private fun findLowPoints(heatMap: HeatMap): List<HeatPoint> {
    val lowPoints = mutableListOf<HeatPoint>()
    for (x in 0 until heatMap.maxX) {
        for (y in 0 until heatMap.maxY) {
            val adjacentValues = heatMap.getAdjacents(x, y)
            val point = heatMap[x, y]
            if (adjacentValues.all { a -> point.value < a.value }) lowPoints.add(point)
        }
    }
    return lowPoints.toList()
}

private fun findBasin(lowPoint: HeatPoint, heatMap: HeatMap): Basin {
    val untouched = mutableListOf(lowPoint)
    val touched = mutableListOf<HeatPoint>()
    while (untouched.isNotEmpty()) {
        val heatPoint = untouched.removeFirst()
        touched.add(heatPoint)
        untouched.addAll(
            heatMap.getAdjacents(heatPoint).filter { a -> a.value > heatPoint.value && a.value < 9 && a !in touched && a !in untouched })
    }
    return Basin(touched.toList())
}

class HeatMap(input: List<List<Int>>) {
    private val map: Array<IntArray> = Array(input.size) { y -> IntArray(input[y].size) { x -> input[y][x] } }
    val maxX: Int
        get() = map.maxOf { it.size }

    val maxY: Int
        get() = map.size

    operator fun get(x: Int, y: Int) = HeatPoint(x, y, map[y][x])

    operator fun set(x: Int, y: Int, v: Int) {
        map[y][x] = v
    }

    fun getAdjacents(heatPoint: HeatPoint): List<HeatPoint> = getAdjacents(heatPoint.x, heatPoint.y)

    fun getAdjacents(x: Int, y: Int): List<HeatPoint> {
        return listOfNotNull(
            map.getOrNull(y - 1)?.getOrNull(x)?.let { HeatPoint(x, y - 1, it) },
            map.getOrNull(y)?.getOrNull(x - 1)?.let { HeatPoint(x - 1, y, it) },
            map.getOrNull(y + 1)?.getOrNull(x)?.let { HeatPoint(x, y + 1, it) },
            map.getOrNull(y)?.getOrNull(x + 1)?.let { HeatPoint(x + 1, y, it) }
        )
    }
}

data class HeatPoint(val x: Int, val y: Int, val value: Int)

data class Basin(val heatPoints: List<HeatPoint>) {
    val size: Int
        get() = heatPoints.size
}
