fun main() {
    fun part1(input: List<String>): Int {
        val position = Position()
        val commands = input.map(Command::from).toList()
        commands.forEach { position.command(it) }
        return position.depth * position.horizontal
    }

    fun part2(input: List<String>): Int {
        val position = Position()
        val commands = input.map(Command::from).toList()
        commands.forEach { position.command2(it) }
        return position.depth * position.horizontal
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}

data class Command(val direction: Direction, val amount: Int) {
    companion object {
        fun from(str: String): Command {
            val (dir, amount) = str.split(" ")
            return Command(Direction.valueOf(dir.uppercase()), amount.toInt())
        }
    }
}

enum class Direction {
    FORWARD,
    DOWN,
    UP
}

data class Position(var horizontal: Int = 0, var depth: Int = 0, var aim: Int = 0) {

    fun command(command: Command) {
        when(command.direction) {
            Direction.FORWARD -> horizontal += command.amount
            Direction.DOWN -> depth += command.amount
            Direction.UP -> depth = (depth - command.amount).coerceAtLeast(0)
        }
    }

    fun command2(command: Command) {
        when (command.direction) {
            Direction.FORWARD -> {
                horizontal += command.amount
                depth = (depth + command.amount * aim).coerceAtLeast(0)
            }
            Direction.DOWN -> aim += command.amount
            Direction.UP -> aim -= command.amount
        }
    }
}