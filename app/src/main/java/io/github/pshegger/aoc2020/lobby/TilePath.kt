package io.github.pshegger.aoc2020.lobby

data class TilePath(val directions: List<HexDirection>) {
    val coords by lazy {
        directions.fold(Pair(0, 0)) { acc, direction -> direction.applyToCoords(acc) }
    }
}
