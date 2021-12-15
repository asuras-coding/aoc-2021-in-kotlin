import org.jetbrains.kotlinx.multik.api.d2array
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.*

// dijkstra mit weighted vertices
// ggf. immer dem kürzesten weg folgen, bis ziel erreicht
// graph bauen mit matrix einmal alle x durch, einmal alle y durch

data class Node(val x: Int, val y: Int, val weight: Int, var visited: Boolean = false)
data class Edge(val x1: Int, val y1: Int, val x2: Int, val y2: Int)

fun main() {
    fun part1(input: List<String>): Int {
        val size = input.first().length
        val map: D2Array<Int> = readMap(input)
        val nodes = mutableListOf<Node>()
        map.forEachMultiIndexed { index, i ->
            val (x, y) = index
            nodes.add(Node(x, y, i))
        }
        val adj = mk.d2array(size, size) { Int.MAX_VALUE }
        nodes.first { it.x == 0 && it.y == 0 }.visited = true
        adj[0, 0] = 0
        while (nodes.any { !it.visited }) {
            val visitedNodes = nodes.filter { it.visited }
            val unvisitedNodes = nodes.filterNot { it.visited }
            // vermutlich ist da noch ein (nicht so schlimmer) fehler drinne
            visitedNodes.forEach { vn ->
                val neighbors = getNeighbors(vn, nodes);
                neighbors.forEach {
                    it.visited = true
                    if (it.weight + adj[vn.x, vn.y] < adj[it.x, it.y]) adj[it.x, it.y] = it.weight + adj[vn.x, vn.y]
                }
            }
        }
        return adj[size-1, size-1]
    }

    fun part2(input: List<String>): Int {
        // schauen, wie man matrizen expandiert oder flatmapt
        // danach kann man für das raster ja die matrix nehmen und mit den koordinaten verrechnen
        // am ende wieder eine große matrix machen und hoffen, das djikstra fix genug ist
        return 0
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
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