import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.*

// dijkstra mit weighted vertices
// ggf. immer dem kürzesten weg folgen, bis ziel erreicht
// graph bauen mit matrix einmal alle x durch, einmal alle y durch

data class Node(val x: Int, val y: Int, val weight: Int, var visited: Boolean = false)

fun main() {
    fun part1(input: List<String>): Int {
        val size = input.first().length
        val map: D2Array<Int> = readMap(input)
        val nodes = mutableListOf<Node>()
        map.forEachMultiIndexed { index, i ->
            val (x, y) = index
            nodes.add(Node(x, y, i))
        }
        return dijkstra(nodes, size)
    }

    // super slow
    fun part2(input: List<String>): Int {
        val baseSize = input.first().length
        val size = baseSize * 5
        val inputArray: D2Array<Int> = readMap(input)
        val map: D2Array<Int> = mk.zeros<Int>(size, size).mapMultiIndexed { index, _ ->
            val (x, y) = index
            val newValue = inputArray[x % baseSize, y % baseSize] + x / baseSize + y / baseSize
            if (newValue > 9) newValue - 9 else newValue
        }
        val nodes = mutableListOf<Node>()
        map.forEachMultiIndexed { index, i ->
            val (x, y) = index
            nodes.add(Node(x, y, i))
        }
        return dijkstra(nodes, size)
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}

fun dijkstra(nodes: List<Node>, size: Int): Int {
    val adj = mk.d2array(size, size) { Int.MAX_VALUE }
    adj[0, 0] = 0
    while (nodes.any { !it.visited }) {
        val unvisitedNodes = nodes.filterNot { it.visited }
        val vn = unvisitedNodes.minByOrNull { adj[it.x, it.y] }!!
        vn.visited = true
        val neighbors = getNeighbors(vn, unvisitedNodes)
        neighbors.forEach {
            if (it.weight + adj[vn.x, vn.y] < adj[it.x, it.y]) adj[it.x, it.y] = it.weight + adj[vn.x, vn.y]
        }
    }
    return adj[size - 1, size - 1]

}

private fun getNeighbors(node: Node, nodes: List<Node>): List<Node> {
    return nodes.filter {
        (it.x == node.x && it.y == node.y + 1) ||
                (it.x == node.x && it.y == node.y - 1) ||
                (it.x == node.x + 1 && it.y == node.y) ||
                (it.x == node.x - 1 && it.y == node.y)
    }
}

private fun readMap(input: List<String>): D2Array<Int> {
    return mk.ndarray(input.map { it.chunked(1).map { c -> c.toInt() } }).transpose()
}