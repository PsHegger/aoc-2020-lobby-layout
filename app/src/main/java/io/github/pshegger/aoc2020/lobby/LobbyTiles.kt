package io.github.pshegger.aoc2020.lobby

class LobbyTiles {
    var tiles = mapOf<Pair<Int, Int>, Boolean>()
    var nextTiles = mapOf<Pair<Int, Int>, Boolean>()
    var unchanged = setOf<Pair<Int, Int>>()
    var coordRange = (-10000..10000)

    fun setInitialTiles(pathList: List<TilePath>) {
        val startingTiles = mutableMapOf<Pair<Int, Int>, Boolean>()

        pathList.forEach { p ->
            startingTiles[p.coords] = !(startingTiles[p.coords] ?: false)
        }

        tiles = startingTiles
        nextTiles = simulateDay()
        unchanged = tiles.keys.toSet().intersect(nextTiles.keys)
    }

    fun nextDay() {
        tiles = nextTiles
        nextTiles = simulateDay()
        unchanged = tiles.keys.toSet().intersect(nextTiles.keys)
    }

    private fun simulateDay() =
        tiles.keys.flatMap { it.adjacentCoords() }
            .toSet()
            .associateWith { c ->
                val adjacentBlacks = c.adjacentCoords().count { tiles[it] == true }
                val currentTile = tiles[c] ?: false
                when {
                    currentTile && adjacentBlacks == 0 -> false
                    currentTile && adjacentBlacks > 2 -> false
                    !currentTile && adjacentBlacks == 2 -> true
                    else -> currentTile
                }
            }
            .filter { it.value && it.key.first in coordRange && it.key.second in coordRange }

    private fun Pair<Int, Int>.adjacentCoords() = HexDirection.values().map { it.applyToCoords(this) }
}
