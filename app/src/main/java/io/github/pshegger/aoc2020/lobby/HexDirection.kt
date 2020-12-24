package io.github.pshegger.aoc2020.lobby

import kotlin.math.absoluteValue

enum class HexDirection(val notation: String) {
    East("e"),
    SouthEast("se"),
    SouthWest("sw"),
    West("w"),
    NorthWest("nw"),
    NorthEast("ne");

    fun applyToCoords(coords: Pair<Int, Int>): Pair<Int, Int> {
        val (x, y) = coords

        return when (this) {
            East -> Pair(x + 1, y)
            SouthEast -> Pair(
                if (y.absoluteValue % 2 == 1) x + 1 else x,
                y - 1
            )
            SouthWest -> Pair(
                if (y.absoluteValue % 2 == 0) x - 1 else x,
                y - 1
            )
            West -> Pair(x - 1, y)
            NorthWest -> Pair(
                if (y.absoluteValue % 2 == 0) x - 1 else x,
                y + 1
            )
            NorthEast -> Pair(
                if (y.absoluteValue % 2 == 1) x + 1 else x,
                y + 1
            )
        }
    }
}
