import java.lang.Exception
import java.lang.Integer.min
import java.lang.Integer.max
import kotlin.math.abs

fun main() {
    fun part1(input: List<String>, size: Int = 1000): Int {
        val vents = parseVents(input)
        val grid = Grid(size)
        vents.forEach { vent ->
            when {
                vent.isHorizontal() -> grid.drawHorizontalVent(vent)
                vent.isVertical() -> grid.drawVerticalVent(vent)
            }
        }
        return grid.countDangerousAreas()
    }

    fun part2(input: List<String>, size: Int = 1000): Int {
        val vents = parseVents(input)
        val grid = Grid(size)
        vents.forEach { vent ->
            when {
                vent.isHorizontal() -> grid.drawHorizontalVent(vent)
                vent.isVertical() -> grid.drawVerticalVent(vent)
                vent.isDiagonal() -> grid.drawDiagonalVent(vent)
            }
        }
        return grid.countDangerousAreas()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day05_test")
    check(part1(testInput, 10) == 5)
    check(part2(testInput, 10) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}

data class Vent(val start: Point, val end: Point) {
    fun isHorizontal(): Boolean {
        return start.x == end.x
    }

    fun isVertical(): Boolean {
        return start.y == end.y
    }

    fun isDiagonal(): Boolean {
        return abs(start.x - end.x) == abs(start.y - end.y)
    }

    fun diagonalDirection(): Diagonal {
        return when {
            start.x < end.x && start.y < end.y -> Diagonal.DOWN_RIGHT
            start.x > end.x && start.y < end.y -> Diagonal.DOWN_LEFT
            start.x < end.x && start.y > end.y -> Diagonal.UP_RIGHT
            start.x > end.x && start.y > end.y -> Diagonal.UP_LEFT
            else -> throw Exception("not a diagonal vent")
        }
    }
}

data class Point(val x: Int, val y: Int)

class Grid(size: Int = 1000) {
    // Note x and y are swapped
    private val gridCells = Array(size) { IntArray(size) { 0 } }

    fun drawHorizontalVent(vent: Vent) {
        val min = min(vent.start.y, vent.end.y)
        val max = max(vent.start.y, vent.end.y)
        IntRange(min, max).forEach { gridCells[it][vent.start.x] = gridCells[it][vent.start.x] + 1 }
    }

    fun drawVerticalVent(vent: Vent) {
        val min = min(vent.start.x, vent.end.x)
        val max = max(vent.start.x, vent.end.x)
        IntRange(min, max).forEach { gridCells[vent.start.y][it] = gridCells[vent.start.y][it] + 1 }
    }

    fun drawDiagonalVent(vent: Vent) {
        // also UP-could be implemented with drawDiagonal(Vent(vent.end, vent.start)) to reverse direction
        when (vent.diagonalDirection()) {
            Diagonal.DOWN_RIGHT -> {
                for ((offset, i) in (vent.start.y..vent.end.y).withIndex()) {
                    gridCells[i][vent.start.x + offset] = gridCells[i][vent.start.x + offset] + 1
                }
            }
            Diagonal.DOWN_LEFT -> {
                var offset = 0
                for (i in (vent.start.y..vent.end.y)) {
                    gridCells[i][vent.start.x + offset] = gridCells[i][vent.start.x + offset] + 1
                    offset--
                }
            }
            Diagonal.UP_LEFT -> {
                var offset = 0
                for (i in (vent.start.y downTo vent.end.y)) {
                    gridCells[i][vent.start.x + offset] = gridCells[i][vent.start.x + offset] + 1
                    offset--
                }
            }
            Diagonal.UP_RIGHT -> {
                for ((offset, i) in ((vent.start.y downTo vent.end.y)).withIndex()) {
                    gridCells[i][vent.start.x + offset] = gridCells[i][vent.start.x + offset] + 1
                }
            }
        }

    }

    fun countDangerousAreas(): Int {
        return gridCells.sumOf { row -> row.count { it >= 2 } }
    }


    fun draw() {
        gridCells.forEach { println(it.joinToString(" ").replace("0", ".")) }
    }
}

enum class Diagonal {
    DOWN_LEFT,
    DOWN_RIGHT,
    UP_LEFT,
    UP_RIGHT
}

fun parseVents(input: List<String>): List<Vent> {
    return input.map {
        val (start, end) = it.split(" -> ")
        val (x1, y1) = start.split(",").map(String::toInt)
        val (x2, y2) = end.split(",").map(String::toInt)
        Vent(Point(x1, y1), Point(x2, y2))
    }
}