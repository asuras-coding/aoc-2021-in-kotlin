fun main() {
    fun part1(input: List<String>): Int {
        val octoGrid = parseOctoGrid(input)
        repeat(100) { octoGrid.step() }
        return octoGrid.lastStepResult.totalFlashes
    }

    fun part2(input: List<String>): Int {
        val octoGrid = parseOctoGrid(input)
        while (!octoGrid.lastStepResult.allFlashed) {
            octoGrid.step()
        }
        return octoGrid.lastStepResult.steps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}

data class Octo(val x: Int, val y: Int, var energy: Int = 0, var flashed: Boolean = false)
data class StepResult(val totalFlashes: Int = 0, val flashes: Int = 0, val steps: Int = 0, val allFlashed: Boolean = false)

class OctoGrid(input: List<List<Int>>) {
    private val grid: Array<Array<Octo>> = Array(input.size) { y -> Array(input[y].size) { x -> Octo(x, y, input[y][x]) } }
    private var flashes = 0
    private var steps = 0
    var lastStepResult: StepResult = StepResult()
        private set

    operator fun get(x: Int, y: Int) = grid[y][x]

    operator fun set(x: Int, y: Int, v: Int) {
        grid[y][x].energy = v
    }

    fun getAdjacents(octo: Octo): List<Octo> = getAdjacents(octo.x, octo.y)

    fun getAdjacents(x: Int, y: Int): List<Octo> {
        return listOfNotNull(
            grid.getOrNull(y - 1)?.getOrNull(x),
            grid.getOrNull(y)?.getOrNull(x - 1),
            grid.getOrNull(y + 1)?.getOrNull(x),
            grid.getOrNull(y)?.getOrNull(x + 1),
            grid.getOrNull(y + 1)?.getOrNull(x + 1),
            grid.getOrNull(y + 1)?.getOrNull(x - 1),
            grid.getOrNull(y - 1)?.getOrNull(x - 1),
            grid.getOrNull(y - 1)?.getOrNull(x + 1)
        )
    }

    fun step(): StepResult {
        steps++
        val flashed = mutableListOf<Octo>()
        for (x in 0..grid.first().lastIndex) {
            for (y in 0..grid.lastIndex) {
                val octo = get(x, y)
                octo.energy = octo.energy + 1
                flashOcto(octo, flashed)
            }
        }
        flashes += flashed.size
        flashed.forEach { it.energy = 0 }
        lastStepResult = StepResult(flashes, flashed.size, steps, flashed.size == grid.size * grid.first().size)
        return lastStepResult
    }

    private fun flashOcto(octo: Octo, flashed: MutableList<Octo>) {
        if (octo.energy > 9 && octo !in flashed) {
            flashed.add(octo)
            getAdjacents(octo).forEach {
                it.energy = it.energy + 1
                flashOcto(it, flashed)
            }
        }
    }
}

private fun parseOctoGrid(input: List<String>): OctoGrid {
    val list = input.map { it.chunked(1).map { a -> a.toInt() } }
    return OctoGrid(list)
}