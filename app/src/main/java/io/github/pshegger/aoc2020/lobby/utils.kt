package io.github.pshegger.aoc2020.lobby

import android.content.Context
import java.util.*

fun Context.readInput(): List<TilePath> {
    val paths = mutableListOf<TilePath>()
    resources.openRawResource(R.raw.input).use {
        val scanner = Scanner(it)
        var line = scanner.nextLineOrNull()
        while (line != null) {
            val directions = mutableListOf<HexDirection>()
            var l = line ?: continue
            while (l.isNotBlank()) {
                val (d, lr) = nextDirection(l)
                directions.add(d)
                l = lr
            }
            paths.add(TilePath(directions))
            line = scanner.nextLineOrNull()
        }
    }
    return paths
}

private fun Scanner.nextLineOrNull() = try {
    nextLine()
} catch (_: NoSuchElementException) {
    null
}

private fun nextDirection(line: String) =
    HexDirection.values().first { line.startsWith(it.notation) }.let { dir ->
        Pair(dir, line.drop(dir.notation.length))
    }
