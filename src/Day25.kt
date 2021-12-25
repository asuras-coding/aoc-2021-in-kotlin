import org.jetbrains.kotlinx.multik.api.*
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.mapMultiIndexed

fun main() {
    fun part1(input: List<String>): Int {
        val initialField = readCucumberField(input)
        var oldField = mk.zeros<Int>(1, 1)
        var recentField = initialField
        var steps = 0
        while (oldField != recentField) {
            steps++
            oldField = recentField
            recentField = moveRight(recentField)
            recentField = moveDown(recentField)
        }
        return steps
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day25_test")
    check(part1(testInput) == 58)

    val input = readInput("Day25")
    println(part1(input))
}

private fun readCucumberField(input: List<String>): D2Array<Int> {
    return mk.d2arrayIndices(input.size, input.first().length) { i, j -> input[i][j].code }
}

// [y,x] 118=v, 46=. 62=>
private fun moveRight(input: D2Array<Int>): D2Array<Int> {
    val sizeX = input.multiIndices.last.last() + 1
    return input.mapMultiIndexed { index, cu ->
        when(cu) {
            46 -> if (input[index.first(),(index.last()-1 + sizeX) % sizeX] == 62) 62 else 46
            62 -> if (input[index.first(),(index.last()+1 + sizeX) % sizeX] == 46) 46 else 62
            else -> cu
        }
    }
}

private fun moveDown(input: D2Array<Int>): D2Array<Int> {
    val sizeY = input.multiIndices.last.first() + 1
    return input.mapMultiIndexed { index, cu ->
        when(cu) {
            46 -> if (input[(index.first()-1 + sizeY) % sizeY,index.last()] == 118) 118 else 46
            118 -> if (input[(index.first()+1 + sizeY) % sizeY,index.last()] == 46) 46 else 118
            else -> cu
        }
    }
}
