fun main() {
    fun part1(input: List<String>): Int {
        val enhancement = input.first()
        var pictureMap: PictureMap = readLightPicture(input.drop(2))
        repeat(2) {
            pictureMap = pictureMap.runEnhancement(enhancement)
        }
        return when (val result = pictureMap) {
            is LightMap -> result.lightPixels
            is DarkMap -> result.darkPixels
        }
    }

    fun part2(input: List<String>): Int {
        val enhancement = input.first()
        var pictureMap: PictureMap = readLightPicture(input.drop(2))
        repeat(50) {
            pictureMap = pictureMap.runEnhancement(enhancement)
        }
        return when (val result = pictureMap) {
            is LightMap -> result.lightPixels
            is DarkMap -> result.darkPixels
        }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day20_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}

private fun readLightPicture(input: List<String>): LightMap {
    return LightMap(input.flatMapIndexed { lineIndex, line ->
        line.mapIndexedNotNull { charIndex, char ->
            if (char == '#') PicturePoint(lineIndex, charIndex) else null
        }
    })
}

data class PicturePoint(val lineIndex: Int, val charIndex: Int)

sealed class PictureMap {
    abstract val map: MutableMap<PicturePoint, Char>
    abstract operator fun get(line: Int, char: Int): Char
    abstract operator fun set(line: Int, char: Int, value: Char)
    abstract fun runEnhancement(enhancement: String): PictureMap

    fun getAdjascentsInOrder(picturePoint: PicturePoint): List<Pair<PicturePoint, Char>> =
        getAdjascentsInOrder(picturePoint.lineIndex, picturePoint.charIndex)

    fun getAdjascentsInOrder(line: Int, char: Int): List<Pair<PicturePoint, Char>> {
        return listOf(
            PicturePoint(line - 1, char - 1) to this[line - 1, char - 1],
            PicturePoint(line - 1, char) to this[line - 1, char],
            PicturePoint(line - 1, char + 1) to this[line - 1, char + 1],
            PicturePoint(line, char - 1) to this[line, char - 1],
            PicturePoint(line, char) to this[line, char],
            PicturePoint(line, char + 1) to this[line, char + 1],
            PicturePoint(line + 1, char - 1) to this[line + 1, char - 1],
            PicturePoint(line + 1, char) to this[line + 1, char],
            PicturePoint(line + 1, char + 1) to this[line + 1, char + 1],
        )
    }

    fun print() {
        val minLine = map.keys.minOf { it.lineIndex }
        val maxLine = map.keys.maxOf { it.lineIndex }
        val minChar = map.keys.minOf { it.charIndex }
        val maxChar = map.keys.maxOf { it.charIndex }
        (minLine..maxLine).forEach { line ->
            println(IntRange(minChar, maxChar).joinToString("") { this[line, it].toString() })
        }
    }
}

class LightMap(picturePoints: List<PicturePoint> = emptyList()) : PictureMap() {
    val lightPixels: Int
        get() = map.size
    override val map: MutableMap<PicturePoint, Char> = picturePoints.associateWith { '#' }.toMutableMap()

    override operator fun get(line: Int, char: Int): Char {
        return map.getOrDefault(PicturePoint(line, char), '.')
    }

    override operator fun set(line: Int, char: Int, value: Char) {
        if (value == '#') map[PicturePoint(line, char)] = '#'
        else map.remove(PicturePoint(line, char))
    }

    override fun runEnhancement(enhancement: String): PictureMap {
        val allRelevantPixels: Set<PicturePoint> = map.keys.flatMap { k -> getAdjascentsInOrder(k).map { it.first } }.toSet()
        return if (enhancement.first() == '.') {
            LightMap(allRelevantPixels.map { p -> p to enhancement[getAdjascentsInOrder(p).toValues().mapToRadix10()] }
                .filter { it.second == '#' }.map { it.first }.toList())
        } else {
            DarkMap(allRelevantPixels.map { p -> p to enhancement[getAdjascentsInOrder(p).toValues().mapToRadix10()] }
                .filter { it.second == '.' }.map { it.first }.toList())
        }
    }
}

class DarkMap(picturePoints: List<PicturePoint> = emptyList()) : PictureMap() {
    val darkPixels: Int
        get() = map.size
    override val map: MutableMap<PicturePoint, Char> = picturePoints.associateWith { '.' }.toMutableMap()

    override operator fun get(line: Int, char: Int): Char {
        return map.getOrDefault(PicturePoint(line, char), '#')
    }

    override operator fun set(line: Int, char: Int, value: Char) {
        if (value == '.') map[PicturePoint(line, char)] = '.'
        else map.remove(PicturePoint(line, char))
    }

    override fun runEnhancement(enhancement: String): PictureMap {
        val allRelevantPixels: Set<PicturePoint> = map.keys.flatMap { k -> getAdjascentsInOrder(k).map { it.first } }.toSet()
        return if (enhancement.last() == '.') {
            LightMap(allRelevantPixels.map { p -> p to enhancement[getAdjascentsInOrder(p).toValues().mapToRadix10()] }
                .filter { it.second == '#' }.map { it.first }.toList())
        } else {
            DarkMap(allRelevantPixels.map { p -> p to enhancement[getAdjascentsInOrder(p).toValues().mapToRadix10()] }
                .filter { it.second == '.' }.map { it.first }.toList())
        }
    }
}

private fun List<Pair<PicturePoint, Char>>.toValues(): String = this.joinToString("") { "${it.second}" }

private fun String.mapToRadix10() = this.replace(".", "0").replace("#", "1").toInt(2)