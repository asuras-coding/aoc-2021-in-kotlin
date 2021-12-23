import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.abs

// Solution by Roman Elizarov (https://github.com/elizarov/AdventOfCode2021)
// I renamed everything to make it more readable, extracted some logic into functions and changed some validations
fun main() {
    fun part1(input: List<String>): Int {
        return solve(input)
    }

    fun part2(input: List<String>): Int {
        return solve(extendInput(input))
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day23_test").subList(1, 4)
    val input = readInput("Day23").subList(1, 4)
    check(part1(testInput) == 12521)
    check(part1(input) == 15109)
    check(part2(testInput) == 44169)
    check(part2(input) == 53751)

    println(part1(input))
    println(part1(extendInput(input)))
}

private fun extendInput(input: List<String>): List<String> = buildList {
    add(input[0])
    add(input[1])
    add("  #D#C#B#A#  ")
    add("  #D#B#A#C#  ")
    add(input[2])
}

private fun solve(input: List<String>): Int {
    val start = State(input.map { it.toCharArray() }.toTypedArray(), 0)
    val queue = PriorityQueue(compareBy(State::energyCost))
    val energyCostMap = HashMap<State, Int>()
    val knownStates = HashSet<State>()
    enqueueState(start, queue, energyCostMap)
    while (true) {
        val state = queue.remove()!!
        if (state in knownStates) continue
        knownStates += state
        val currentEnergyCost = energyCostMap[state]!!
        if (isEndState(state)) return state.energyCost
        val floor = state.lines.first()

        val floorMoves = findMovesFloorToRoom(floor, state, currentEnergyCost)
        enqueueAllStates(floorMoves, queue, energyCostMap)
        val roomMoves = moveRoomToFloor(state, floor, currentEnergyCost)
        enqueueAllStates(roomMoves, queue, energyCostMap)
    }
}

private fun findMovesFloorToRoom(
    floor: CharArray,
    state: State,
    energyCost: Int
): List<State> {
    return (1..11).mapNotNull { floorIndex ->
        val charFloorIndex = floor[floorIndex]
        if (charFloorIndex !in 'A'..'D') return@mapNotNull null
        val matchingRoomNumber = charFloorIndex - 'A'
        val matchingRoomFloorIndex = calculateRoomIndex(matchingRoomNumber)
        if (!isPathBetweenFree(floorIndex, matchingRoomFloorIndex, floor)) return@mapNotNull null
        val room = getRoomFromState(state, matchingRoomFloorIndex)
        if (isRoomFull(room) || containsWrongPods(room, charFloorIndex)) return@mapNotNull null
        val freePosition = lastFreePositionInRoom(room)
        val nextState =
            state.copy(energyCost + calculateEnergyCostForMove(charFloorIndex, floorIndex, matchingRoomFloorIndex, freePosition))
        moveIntoRoom(nextState, charFloorIndex, floorIndex, matchingRoomFloorIndex, freePosition)
        nextState
    }
}

private fun moveRoomToFloor(
    state: State,
    floor: CharArray,
    energyCost: Int
): List<State> {
    return (0..3).flatMap { roomNumber ->
        val roomIndex = calculateRoomIndex(roomNumber)
        val room = getRoomFromState(state, roomIndex)
        val occupiedPosition = getFirstOccupiedPositionInRoom(room)
        if (occupiedPosition == 0) return@flatMap emptyList()
        val charInRoom = room[occupiedPosition - 1]
        findAllFloorMovesForCharInRoom(state, energyCost, roomIndex, floor, charInRoom, occupiedPosition)
    }
}

private fun findAllFloorMovesForCharInRoom(
    state: State,
    energyCost: Int,
    roomIndex: Int,
    floor: CharArray,
    charInRoom: Char,
    occupiedPosition: Int
): List<State> {
    return (1..11).mapNotNull { floorIndex ->
        if (isDoorEntry(floorIndex)) return@mapNotNull null
        if (!isPathAndPositionFree(floorIndex, roomIndex, floor)) return@mapNotNull null
        val nextState =
            state.copy(energyCost + calculateEnergyCostForMove(charInRoom, floorIndex, roomIndex, occupiedPosition))
        moveFromRoom(nextState, charInRoom, floorIndex, roomIndex, occupiedPosition)
        nextState
    }
}

data class State(val lines: Array<CharArray>, val energyCost: Int = 0) { //c = input, currentState | d = distance?
    override fun equals(other: Any?): Boolean =
        other is State && lines.mapIndexed { index, chars -> chars.contentEquals(other.lines[index]) }.all { it }

    override fun hashCode(): Int = lines.fold(0) { acc, chars -> acc * 31 + chars.contentHashCode() }
    fun copy(d1: Int) = State(lines.map { it.copyOf() }.toTypedArray(), d1)
    override fun toString(): String = buildList {
        add("#".repeat(13))
        addAll(lines.map { it.concatToString() })
        add("distance=$energyCost")
    }.joinToString("\n")
}


fun cost(c: Char): Int = when (c) {
    'A' -> 1
    'B' -> 10
    'C' -> 100
    'D' -> 1000
    else -> error("$c")
}

/**
 * If we already know a cheaper path, don't enqueue the state
 */
fun enqueueState(state: State, queue: PriorityQueue<State>, energyCostMap: HashMap<State, Int>) {
    val knownEnergyCost = energyCostMap[state] ?: Int.MAX_VALUE
    if (state.energyCost >= knownEnergyCost) return
    energyCostMap[state] = state.energyCost
    queue += state
}

fun enqueueAllStates(states: List<State>, queue: PriorityQueue<State>, energyCostMap: HashMap<State, Int>) {
    states.forEach { enqueueState(it, queue, energyCostMap) }
}

// fancy magic -> iterate all lines and then for each room calculate index of the room and
// check if char[index] belongs to the room, if so return energyCost
fun isEndState(state: State): Boolean {
    for (line in state.lines.drop(1)) {
        for (roomNumber in 0..3) {
            if (line[calculateRoomIndex(roomNumber)] != 'A' + roomNumber) {
                return false
            }
        }
    }
    return true
}

fun calculateRoomIndex(roomNumber: Int) = 2 * roomNumber + 3

fun isPathBetweenFree(startPosition: Int, endPosition: Int, path: CharArray) =
    path.slice(startPosition positiveRangeExclusive endPosition).all { it == '.' }

fun isPathAndPositionFree(startPosition: Int, endPosition: Int, path: CharArray) =
    (startPosition positiveRange endPosition).all { index -> path[index] == '.' }

infix fun Int.positiveRangeExclusive(other: Int): IntRange = minOf(this, other) + 1 until maxOf(this, other)
infix fun Int.positiveRange(other: Int): IntRange = minOf(this, other)..maxOf(this, other)

fun getRoomFromState(state: State, roomIndex: Int) = state.lines.drop(1).map { it[roomIndex] }.toCharArray()

fun isRoomFull(room: CharArray) = room.none { it == '.' }

fun containsWrongPods(room: CharArray, pod: Char) = room.any { it !in listOf(pod, '.') }

fun lastFreePositionInRoom(room: CharArray): Int = room.indexOfLast { it == '.' } + 1

fun calculateEnergyCostForMove(char: Char, startIndex: Int, endIndex: Int, roomPosition: Int) =
    cost(char) * (abs(startIndex - endIndex) + roomPosition)

fun moveIntoRoom(state: State, char: Char, floorIndex: Int, roomIndex: Int, roomPosition: Int) {
    state.lines[0][floorIndex] = '.'
    state.lines[roomPosition][roomIndex] = char
}

fun moveFromRoom(state: State, char: Char, floorIndex: Int, roomIndex: Int, roomPosition: Int) {
    state.lines[0][floorIndex] = char
    state.lines[roomPosition][roomIndex] = '.'
}

fun getFirstOccupiedPositionInRoom(room: CharArray): Int = room.indexOfFirst { it in 'A'..'D' } + 1

fun isDoorEntry(floorIndex: Int) = (floorIndex - 3) % 2 == 0 && (floorIndex - 3) / 2 in 0..3
