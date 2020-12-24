package io.github.pshegger.aoc2020.lobby

import android.content.Context
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlin.math.*

class LobbySurfaceView(context: Context, attrs: AttributeSet?, defStyleAttrs: Int, defStyleRes: Int) :
    SurfaceView(context, attrs, defStyleAttrs, defStyleRes), SurfaceHolder.Callback {

    constructor(context: Context) : this(context, null, 0, 0)
    constructor(context: Context, atts: AttributeSet?) : this(context, atts, 0, 0)
    constructor(context: Context, atts: AttributeSet?, defStyleAttr: Int) : this(context, atts, defStyleAttr, 0)

    private var renderThread = RenderThread(false)
    private val palettes = (1..2).map { ColorPalette.load(context, it) }
    private var paletteIndex = 1
    private var origin = Pair(0f, 0f)
    private val lobby = LobbyTiles()

    private val paint = Paint().apply {
        style = Paint.Style.FILL
    }
    private val path = Path()

    init {
        holder.addCallback(this)
        lobby.setInitialTiles(context.readInput())
    }

    override fun surfaceCreated(holder: SurfaceHolder?) {}

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        origin = Pair(width / 2f, height / 2f)

        val xHexCount = (width / HEX_WIDTH * 2f).roundToInt()
        val yHexCount = (height / HEX_HEIGHT * 2f).roundToInt()
        val maxHexCount = max(xHexCount, yHexCount)
        lobby.coordRange = (-maxHexCount / 2)..(maxHexCount / 2)

        renderThread.running = false
        renderThread = RenderThread()
        renderThread.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder?) {
        renderThread.running = false
    }

    private inner class RenderThread(var running: Boolean = true) : Thread() {
        override fun run() {
            var lastUpdate = System.currentTimeMillis()
            var frameCounter = 0

            while (running) {
                val canvas = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    holder.lockHardwareCanvas()
                } else {
                    holder.lockCanvas()
                } ?: break

                if (System.currentTimeMillis() - lastUpdate > UPDATE_INTERVAL) {
                    lobby.nextDay()
                    lastUpdate = System.currentTimeMillis()
                    frameCounter = 0
                }

                canvas.drawColor(palettes[paletteIndex].bgColor)

                paint.color = palettes[paletteIndex].aliveColor
                lobby.tiles.keys.forEach { c ->
                    val mode = if (c in lobby.unchanged) RenderMode.Normal else RenderMode.Shrinking
                    canvas.drawPath(c.toHexPath(frameCounter, mode), paint)
                }
                lobby.nextTiles.keys.forEach { c ->
                    val mode = if (c in lobby.unchanged) RenderMode.Normal else RenderMode.Growing
                    canvas.drawPath(c.toHexPath(frameCounter, mode), paint)
                }

                frameCounter++
                holder.unlockCanvasAndPost(canvas)
            }
        }

        private fun Pair<Int, Int>.toHexPath(frameCounter: Int, renderMode: RenderMode): Path {
            path.reset()
            var cx = origin.first + first * HEX_WIDTH
            if (second.absoluteValue % 2 == 1) {
                cx += HEX_WIDTH / 2
            }

            val cy = origin.second - second * HEX_HEIGHT * 0.75f
            var angleRad = (PI / 180 * (60 * 0 - 30)).toFloat()

            val size = when (renderMode) {
                RenderMode.Normal -> TILE_SIZE
                RenderMode.Shrinking -> max(
                    TILE_SIZE * ((ANIMATION_FRAME_COUNT - frameCounter) / ANIMATION_FRAME_COUNT),
                    0f
                )
                RenderMode.Growing -> min(TILE_SIZE * (frameCounter / ANIMATION_FRAME_COUNT), TILE_SIZE)
            }

            path.moveTo(
                cx + size * cos(angleRad),
                cy + size * sin(angleRad)
            )
            (1..5).forEach {
                angleRad = (PI / 180 * (60 * it - 30)).toFloat()
                path.lineTo(
                    cx + size * cos(angleRad),
                    cy + size * sin(angleRad)
                )
            }
            path.close()

            return path
        }


    }

    private enum class RenderMode {
        Normal, Shrinking, Growing
    }

    companion object {
        private const val UPDATE_INTERVAL = 1000
        private const val ANIMATION_FRAME_COUNT = 30f
        private const val TILE_SIZE = 30f
        private val HEX_WIDTH = sqrt(3f) * TILE_SIZE
        private const val HEX_HEIGHT = 2 * TILE_SIZE
    }
}
