fun main() {
    fun part1(input: List<String>): Int {
        val points = input.filter { it.matches("\\d+,\\d+".toRegex()) }.map {
            val (x, y) = it.split(",")
            Point13(x.toInt(), y.toInt())
        }
        val folds = input.filter { it.startsWith("fold") }.map {
            val (axis, value) = it.substringAfterLast(" ").split("=")
            Fold(Axis.valueOf(axis.uppercase()), value.toInt())
        }
        val page = Page(points.maxByOrNull { it.x }!!.x + 1, points.maxByOrNull { it.y }!!.y + 1, points).fold(folds.first())
        return page.countDots()
    }

    fun part2(input: List<String>): Int {
        val points = input.filter { it.matches("\\d+,\\d+".toRegex()) }.map {
            val (x, y) = it.split(",")
            Point13(x.toInt(), y.toInt())
        }
        val folds = input.filter { it.startsWith("fold") }.map {
            val (axis, value) = it.substringAfterLast(" ").split("=")
            Fold(Axis.valueOf(axis.uppercase()), value.toInt())
        }
        var page = Page(points.maxByOrNull { it.x }!!.x + 1, points.maxByOrNull { it.y }!!.y + 1, points)
        folds.forEachIndexed { i, it ->
            page = page.fold(it)
        }
        page.print()
        return page.countDots()
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}

data class Fold(val axis: Axis, val index: Int)
data class Point13(val x: Int, val y: Int)

enum class Axis {
    X,
    Y
}

class Page(val sizeX: Int, val sizeY: Int) {
    constructor(sizeX: Int, sizeY: Int, input: List<Point13>) : this(sizeX, sizeY) {
        page = Array(sizeY) { Array(sizeX) { "." } }
        input.forEach { set(it.x, it.y, "#") }
    }

    constructor(sizeX: Int, sizeY: Int, page: Array<Array<String>>) : this(sizeX, sizeY) {
        this.page = page
    }

    private lateinit var page: Array<Array<String>>

    operator fun get(x: Int, y: Int) = page[y][x]

    operator fun set(x: Int, y: Int, v: String) {
        page[y][x] = v
    }

    fun fold(fold: Fold): Page {
        return when (fold.axis) {
            Axis.X -> foldX(fold.index)
            Axis.Y -> foldY(fold.index)
        }
    }

    fun print() {
        for (y in 0 until sizeY) {
            println(buildString {
                for (x in 0 until sizeX) {
                    append(page[y][x])
                }
            })
        }
    }

    private fun foldX(index: Int): Page {
        val page = Page(index, sizeY, this.page.map { it.take(index).toTypedArray() }.toTypedArray())
        val fold = this.page.map { it.drop(index + 1).toTypedArray() }.toTypedArray()
        for (x in fold.first().indices) {
            for (y in fold.indices) {
                if (fold[y][x] == "#") page[page.sizeX - 1 - x, y] = "#"
            }
        }
        return page
    }

    private fun foldY(index: Int): Page {
        val page = Page(sizeX, index, this.page.take(index).toTypedArray())
        val fold = this.page.drop(index + 1).toTypedArray()
        for (x in fold.first().indices) {
            for (y in fold.indices) {
                if (fold[y][x] == "#") page[x, page.sizeY - 1 - y] = "#"
            }
        }
        return page
    }

    fun countDots(): Int = page.sumOf { it.count { s -> s == "#" } }
}
