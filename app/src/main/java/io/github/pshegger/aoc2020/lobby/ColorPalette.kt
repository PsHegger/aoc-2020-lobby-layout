package io.github.pshegger.aoc2020.lobby

import android.content.Context

data class ColorPalette(val bgColor: Int, val aliveColor: Int) {

    companion object {
        fun load(context: Context, n: Int) =
            ColorPalette(
                context.getColor("palette${n}Bg"),
                context.getColor("palette${n}Alive"),
            )

        private fun Context.getColor(name: String) =
            resources.getColor(resources.getIdentifier(name, "color", packageName))
    }
}
