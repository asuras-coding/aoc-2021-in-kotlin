fun main() {
    fun part1(input: List<String>, lineNumber: Int = 1): Int {
        val binString = input[lineNumber - 1].hexToBin()
        val packet = parsePacket(binString)
        return packet.second.versionSum
    }

    fun part2(input: List<String>, lineNumber: Int = 1): Long {
        val binString = input[lineNumber - 1].hexToBin()
        val packet = parsePacket(binString)
        return packet.second.value
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day16_test")
    check(part1(testInput, 1) == 6)
    check(part1(testInput, 2) == 16)
    check(part1(testInput, 3) == 12)
    check(part1(testInput, 4) == 23)
    check(part1(testInput, 5) == 31)
    check(part2(testInput, 6) == 3L)
    check(part2(testInput, 7) == 54L)
    check(part2(testInput, 8) == 7L)
    check(part2(testInput, 9) == 9L)
    check(part2(testInput, 10) == 1L)

    val input = readInput("Day16")
    println(part1(input))
    println(part2(input))
}


private fun String.hexToBin(): String =
    this.map { it.toString().toInt(16).toString(2).padStart(4, '0') }.joinToString("")

private fun String.binToHex(): String =
    this.chunked(4).joinToString("") { it.toInt(2).toString(16).uppercase() }

private fun parsePacket(input: String, startIndex: Int = 0): Pair<Int, Packet> {
    var index = startIndex
    val version = input.substring(index, index + 3).toInt(2)
    index += 3
    val typeId = input.substring(index, index + 3).toInt(2)
    index += 3
    val packet = when (typeId) {
        0 -> {
            val literal = Packet.Operator.Sum(version, typeId)
            index = literal.readPacketData(index, input)
            literal
        }
        1 -> {
            val literal = Packet.Operator.Prod(version, typeId)
            index = literal.readPacketData(index, input)
            literal
        }
        2 -> {
            val literal = Packet.Operator.Min(version, typeId)
            index = literal.readPacketData(index, input)
            literal
        }
        3 -> {
            val literal = Packet.Operator.Max(version, typeId)
            index = literal.readPacketData(index, input)
            literal
        }
        4 -> {
            val literal = Packet.Literal(version, typeId)
            index = literal.readPacketData(index, input)
            literal
        }
        5 -> {
            val literal = Packet.Operator.GT(version, typeId)
            index = literal.readPacketData(index, input)
            literal
        }
        6 -> {
            val literal = Packet.Operator.LT(version, typeId)
            index = literal.readPacketData(index, input)
            literal
        }
        7 -> {
            val literal = Packet.Operator.EQ(version, typeId)
            index = literal.readPacketData(index, input)
            literal
        }
        else -> TODO()
    }
    return Pair(index, packet)
}

sealed class Packet {
    abstract val version: Int // first three bits
    abstract val packeTypeId: Int // next three bits
    abstract val versionSum: Int
    abstract val value: Long

    class Literal(override val version: Int, override val packeTypeId: Int) : Packet() { // packetTypeId 4
        override var value: Long = 0
        override val versionSum: Int
            get() = version

        fun readPacketData(index: Int, input: String): Int {
            var newIndex = index
            var hasNext = true
            var readNumbers = ""
            while (hasNext) {
                hasNext = input[newIndex] == '1'
                newIndex++
                readNumbers += input.substring(newIndex, newIndex + 4)
                newIndex += 4
            }
            value = readNumbers.toLong(2)
            return newIndex
        }
    }

    sealed class Operator(override val version: Int, override val packeTypeId: Int) : Packet() { // otherPaketTypeIds
        val subPackages = mutableListOf<Packet>()
        var lengthTypeId = -1
        override val versionSum: Int
            get() = version + subPackages.sumOf { it.versionSum }

        fun readPacketData(index: Int, input: String): Int {
            var newIndex = index
            lengthTypeId = input[newIndex].toString().toInt()
            newIndex++
            return if (lengthTypeId == 0) {
                readByLength(newIndex, input)
            } else {
                readByNumber(newIndex, input)
            }
        }

        private fun readByLength(index: Int, input: String): Int {
            var newIndex = index
            val subPacketLength = input.substring(newIndex, newIndex + 15).toInt(2)
            newIndex += 15
            while (newIndex < index + 15 + subPacketLength) {
                val packetPair = parsePacket(input, newIndex)
                newIndex = packetPair.first
                subPackages.add(packetPair.second)
            }
            return newIndex
        }

        private fun readByNumber(index: Int, input: String): Int {
            var newIndex = index
            val subPacketCount = input.substring(newIndex, newIndex + 11).toInt(2)
            newIndex += 11
            repeat(subPacketCount) {
                val packetPair = parsePacket(input, newIndex)
                newIndex = packetPair.first
                subPackages.add(packetPair.second)
            }
            return newIndex
        }

        class Sum(version: Int, packeTypeId: Int) : Operator(version, packeTypeId) { // id = 0
            override val value: Long
                get() = subPackages.sumOf { it.value }
        }

        class Prod(version: Int, packeTypeId: Int) : Operator(version, packeTypeId) { // id = 1
            override val value: Long
                get() = subPackages.fold(1L) { acc, packet -> acc * packet.value }
        }

        class Min(version: Int, packeTypeId: Int) : Operator(version, packeTypeId) { // id = 2
            override val value: Long
                get() = subPackages.minOf { it.value }
        }

        class Max(version: Int, packeTypeId: Int) : Operator(version, packeTypeId) { // id = 3
            override val value: Long
                get() = subPackages.maxOf { it.value }
        }

        class GT(version: Int, packeTypeId: Int) : Operator(version, packeTypeId) { // id = 5
            override val value: Long
                get() = if (subPackages[0].value > subPackages[1].value) 1 else 0
        }

        class LT(version: Int, packeTypeId: Int) : Operator(version, packeTypeId) { // id = 6
            override val value: Long
                get() = if (subPackages[0].value < subPackages[1].value) 1 else 0
        }

        class EQ(version: Int, packeTypeId: Int) : Operator(version, packeTypeId) { // id = 7
            override val value: Long
                get() = if (subPackages[0].value == subPackages[1].value) 1 else 0
        }
    }
}