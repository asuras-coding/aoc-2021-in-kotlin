fun main() {
    fun part1(input: List<String>): Int {
        val caveMap = createCaveMap(input)
        val cavePaths = caveMap.findPaths().toSet()
        return cavePaths.size
    }

    fun part2(input: List<String>): Int {
        val caveMap = createCaveMap(input)
        val cavePaths = caveMap.findPaths2().toSet()
        return cavePaths.size
    }

    // test if implementation meets criteria from the description, like:
    val testInput1 = readInput("Day12_test1")
    val testInput = readInput("Day12_test")
    check(part1(testInput1) == 10)
    check(part1(testInput) == 226)
    val testInput2 = readInput("Day12_test1")
    val testInput3 = readInput("Day12_test")
    check(part2(testInput2) == 36)
    check(part2(testInput3) == 3509)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}

private fun createCaveMap(input: List<String>): CaveMap {
    val nodes = mutableSetOf<CaveNode>()
    val edges = mutableSetOf<CaveEdge>()
    input.forEach {
        val (startNode, endNode) = it.split("-")
        val start = CaveNode(startNode)
        val end = CaveNode(endNode)
        nodes.add(start)
        nodes.add(end)
        edges.add(CaveEdge(start, end))
        edges.add(CaveEdge(end, start))
    }
    return CaveMap(nodes, edges)
}

data class CaveMap(val nodes: Set<CaveNode>, val edges: Set<CaveEdge>) {
    fun findPaths(): List<CavePath> {
        val startNode = nodes.first { it.name == "start" }
        val endNode = nodes.first { it.name == "end" }
        return dfs(CavePath(listOf(startNode), listOf(startNode)), endNode)
    }

    private fun dfs(path: CavePath, endNode: CaveNode): List<CavePath> {
        if (path.nodes.last() == endNode) return listOf(path)
        val adjNodes = edges.filter { it.startNode == path.nodes.last() }.filter { it.endNode !in path.visitedNodes }.map { it.endNode }
        return adjNodes.map { node ->
            val visitedNodes = if (node.isSmall) path.visitedNodes.plus(node) else path.visitedNodes
            val newPath = CavePath(path.nodes.plus(node), visitedNodes)
            dfs(newPath, endNode)
        }.flatten()
    }

    fun findPaths2(): List<CavePath> {
        val startNode = nodes.first { it.name == "start" }
        val endNode = nodes.first { it.name == "end" }
        return dfs2(CavePath(listOf(startNode), listOf(startNode)), endNode)
    }

    private fun dfs2(path: CavePath, endNode: CaveNode, visitedSmallNode: CaveNode? = null): List<CavePath> {
        if (path.nodes.last() == endNode) return listOf(path)
        val adjNodes = edges.filter { it.startNode == path.nodes.last() }.filter { it.endNode.name != "start" && (it.endNode !in path.visitedNodes || visitedSmallNode == null) }.map { it.endNode }
        return adjNodes.map { node ->
            val smallNode = if (node in path.visitedNodes && visitedSmallNode == null) node else visitedSmallNode
            val visitedNodes = if (node.isSmall) path.visitedNodes.plus(node) else path.visitedNodes
            val newPath = CavePath(path.nodes.plus(node), visitedNodes)
            dfs2(newPath, endNode, smallNode)
        }.flatten()
    }
}

data class CaveNode(val name: String) {
    val isSmall = !name.all { it.isUpperCase() }
}

data class CaveEdge(val startNode: CaveNode, val endNode: CaveNode)

data class CavePath(val nodes: List<CaveNode> = listOf(), val visitedNodes: List<CaveNode> = listOf())
