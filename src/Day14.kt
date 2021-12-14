fun main() {
    fun part1(input: List<String>): Int {
        val steps = 10
        val insertionRules = input.drop(2).map {
            val (pattern, insertion) = it.split(" -> ")
            InsertionRule(pattern, pattern[0] + insertion + pattern[1])
        }
        val polymer = Polymer(input.first(), insertionRules)

        repeat(steps) {
            polymer.expand()
        }

        val mostCommon = polymer.value.groupBy { it }.maxOf { it.value.size } // 1749
        val leastCommon = polymer.value.groupBy { it }.minOf { it.value.size }  // 161
        return mostCommon - leastCommon
    }

    // wir teilen das polymer in window 2 liste auf, packen diese in eine map mit deren anzahl
    // jede iteration nehmen wir nun alle teil-polys und wenden die regeln darauf an, diese teilen wir wieder in window 2 und multiplizieren
    // die anzahl mit der Anzahl des Basis-Polys und fügen diese in die Map hinzu
    // Am Ende
    fun part2(input: List<String>): Long {
        val steps = 40
        val insertionRules = input.drop(2).map {
            val (pattern, insertion) = it.split(" -> ")
            InsertionRule(pattern, insertion)
        }
        val polymer = input.first()
        var polymerMap: MutableMap<String, Long> = polymer.windowed(2).groupBy { it }.toList().associate { it.first to it.second.size.toLong() }.toMutableMap()
        repeat(steps) {
            val newPolymerMap = mutableMapOf<String, Long>()
            val newPairs = polymerMap.keys.map { poly ->
                val insertion = insertionRules.first {it.pattern == poly }.insertion
                Expanded(poly, insertion)
            }
            newPairs.forEach {
                it.insertions.forEach { ins ->
                    newPolymerMap[ins] = newPolymerMap.getOrDefault(ins, 0) + polymerMap[it.key]!!
                }
            }
            polymerMap = newPolymerMap
        }
        // sum up all the 2nd chars of every 2-poly
        val map: MutableMap<Char, Long> = polymerMap.map { it.key.last() to it.value }.groupBy { it.first }.map { it.key to it.value.sumOf { it.second } }.toMap().toMutableMap()
        // add first chars
        map[polymer.first()] = map.getOrDefault(polymer.first(), 0) + polymerMap.values.first()

        return map.maxOf { it.value } - map.minOf { it.value }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
    check(part2(testInput) == 2188189693529)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}

class Expanded(val key: String, insertion: String) {
    val insertions = listOf(key.first() + insertion, insertion + key.last())
}

data class InsertionRule(val pattern: String, val insertion: String)

data class Polymer(var value: String, val insertionRules: List<InsertionRule>) {
    fun expand() {
        value = value.first() + expand(value)
    }

    private fun expand(init: String): String {
        return init.windowed(2).joinToString("") { w -> insertionRules.firstOrNull { it.pattern == w }?.insertion?.drop(1) ?: w.drop(1) }
    }
}