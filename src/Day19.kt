import kotlin.math.abs

fun main() {
    fun part1(input: List<String>): Int {
        val scanners = readScanners(input)
        val masterScanner = mergeScanners(scanners)
        return masterScanner.beacons.size
    }

    fun part2(input: List<String>): Int {
        val scanners = readScanners(input)
        val masterScanner = mergeScanners(scanners)
        val scannerPairs = cartesianProduct(masterScanner.mergedScannerPositions, masterScanner.mergedScannerPositions)

        return scannerPairs.maxOf { it.first.manhatten(it.second) }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day19_test")
    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}

private fun mergeScanners(scanners: List<BeaconScanner>): BeaconScanner {
    val masterScanner = scanners.first { it.number == 0 }
    val unmergedScanners = scanners.toMutableList()
    unmergedScanners@ while (unmergedScanners.size > 1) {
        val scannerPairs = cartesianProduct(unmergedScanners, unmergedScanners).filterNot { it.first == it.second }
        for ((scannerA, scannerB) in scannerPairs) {
            if (tryMerge(scannerA, scannerB, unmergedScanners)) continue@unmergedScanners
        }
    }
    tryMerge(masterScanner, unmergedScanners.first(), unmergedScanners)
    return masterScanner
}

private fun tryMerge(
    scannerA: BeaconScanner,
    scannerB: BeaconScanner,
    unmergedScanners: MutableList<BeaconScanner>
): Boolean {
    val scannerBVariants = generateRotations(scannerB)
    val differencesA: Map<Beacon, Set<Triple<Int, Int, Int>>> = calculateDifferencesBetweenEachPoint(scannerA)
    for (scannerBVariant in scannerBVariants) {
        val differencesB: Map<Beacon, Set<Triple<Int, Int, Int>>> = calculateDifferencesBetweenEachPoint(scannerBVariant)
        for ((beaconA, diffA) in differencesA) {
            for ((beaconB, diffB) in differencesB) {
                if (is12PointsIntersection(diffA, diffB)) {
                    val normedScannerB = normScannerByTwoPoints(scannerBVariant, beaconA, beaconB)
                    if (is12PointsIntersection(scannerA, normedScannerB)) {
                        scannerA.beacons.addAll(normedScannerB.beacons)
                        scannerA.mergedScannerPositions.addAll(normedScannerB.mergedScannerPositions)
                        unmergedScanners.remove(scannerB)
                        return true
                    }
                }
            }
        }
    }
    return false
}

private fun generateRotations(scanner: BeaconScanner): Set<BeaconScanner> {
    val scannerRotations = mutableSetOf(scanner)
    for (x in 0..3) {
        for (y in 0..3) {
            for (z in 0..3) {
                scannerRotations.add(scanner.rotateX(x).rotateY(y).rotateZ(z))
            }
        }
    }
    return scannerRotations.toSet()
}

private fun calculateDifferencesBetweenEachPoint(scanner: BeaconScanner): Map<Beacon, Set<Triple<Int, Int, Int>>> =
    scanner.beacons.associateWith { beacon -> scanner.beacons.associateWith { beacon }.map { it.key.distanceTo(it.value) }.toSet() }

private fun is12PointsIntersection(diffA: Set<Triple<Int, Int, Int>>, diffB: Set<Triple<Int, Int, Int>>): Boolean {
    val intersection = diffA intersect diffB
    return intersection.size >= 12
}

private fun is12PointsIntersection(scannerA: BeaconScanner, scannerB: BeaconScanner): Boolean {
    val intersection = scannerA.beacons intersect scannerB.beacons
    return intersection.size >= 12
}

private fun normScannerByTwoPoints(
    scanner: BeaconScanner,
    baseBeacon: Beacon,
    scannerBeacon: Beacon
): BeaconScanner {
    val distX = scannerBeacon.x - baseBeacon.x
    val distY = scannerBeacon.y - baseBeacon.y
    val distZ = scannerBeacon.z - baseBeacon.z
    return BeaconScanner(
        scanner.number,
        scanner.beacons.map { Beacon(it.x - distX, it.y - distY, it.z - distZ) }.toMutableSet(),
        scanner.mergedScannerPositions.map { Beacon(it.x - distX, it.y - distY, it.z - distZ) }.toMutableSet()
    )
}

private fun readScanners(input: List<String>): List<BeaconScanner> {
    val scanners = mutableListOf<BeaconScanner>()
    input.forEach { line ->
        when {
            line.startsWith("---") -> scanners.add(BeaconScanner(line.split(" ")[2].toInt()))
            line.isNotBlank() -> {
                val (x, y, z) = line.split(",").map { it.toInt() }
                scanners.last().beacons.add(Beacon(x, y, z))
            }
        }
    }
    return scanners.toList()
}

data class BeaconScanner(
    val number: Int,
    val beacons: MutableSet<Beacon> = mutableSetOf(),
    val mergedScannerPositions: MutableSet<Beacon> = mutableSetOf(Beacon(0, 0, 0))
) {
    fun rotateX(amount: Int): BeaconScanner = BeaconScanner(
        number,
        beacons.map { it.rotateX(amount) }.toMutableSet(),
        mergedScannerPositions.map { it.rotateX(amount) }.toMutableSet()
    )

    fun rotateY(amount: Int): BeaconScanner = BeaconScanner(
        number,
        beacons.map { it.rotateY(amount) }.toMutableSet(),
        mergedScannerPositions.map { it.rotateY(amount) }.toMutableSet()
    )

    fun rotateZ(amount: Int): BeaconScanner = BeaconScanner(
        number,
        beacons.map { it.rotateZ(amount) }.toMutableSet(),
        mergedScannerPositions.map { it.rotateZ(amount) }.toMutableSet()
    )
}

data class Beacon(val x: Int, val y: Int, val z: Int) {
    fun rotateX() = Beacon(x, z, -y)
    fun rotateX(amount: Int): Beacon {
        var beacon = this
        repeat(amount) { beacon = beacon.rotateX() }
        return beacon
    }

    fun rotateY() = Beacon(z, y, -x)
    fun rotateY(amount: Int): Beacon {
        var beacon = this
        repeat(amount) { beacon = beacon.rotateY() }
        return beacon
    }

    fun rotateZ() = Beacon(y, -x, z)
    fun rotateZ(amount: Int): Beacon {
        var beacon = this
        repeat(amount) { beacon = beacon.rotateZ() }
        return beacon
    }

    fun distanceTo(other: Beacon): Triple<Int, Int, Int> = Triple(this.x - other.x, this.y - other.y, this.z - other.z)

    fun manhatten(other: Beacon): Int {
        val (x, y, z) = this.distanceTo(other)
        return abs(x) + abs(y) + abs(z)
    }
}