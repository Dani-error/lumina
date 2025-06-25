@file:Suppress("unused")

package dev.dani.lumina.api.scoreboard

import dev.dani.lumina.api.animation.Animated


/*
 * Project: lumina
 * Created at: 24/06/2025 22:56
 * Created by: Dani-error
 */
fun <P> scoreboard(block: ScoreboardBuilder<P>.(P) -> Unit): ScoreboardAdapterFactory<P> =
    object : ScoreboardAdapterFactory<P> {
        override fun create(viewer: P): ScoreboardAdapter {
            return ScoreboardBuilder(viewer).apply { block(viewer) }.build()
        }
    }

typealias ScoreboardTitle = Animated<String>
typealias ScoreboardLine = Animated<String>


class ScoreboardBuilder<P>(val player: P) {
    var style: ScoreboardStyle = ScoreboardStyle.up()

    private var _title: ScoreboardTitle = Animated.Static("")
    private val _lines = mutableListOf<ScoreboardLine>()

    fun title(value: String) {
        _title = Animated.Static(value)
    }

    fun title(animated: ScoreboardTitle) {
        _title = animated
    }

    fun lines(block: LinesBuilder.() -> Unit) {
        LinesBuilder(_lines).apply(block)
    }

    fun build(): ScoreboardAdapter = object : ScoreboardAdapter {
        override val title = _title
        override val lines = _lines.toList()
        override val style = this@ScoreboardBuilder.style
    }
}

class LinesBuilder(private val lines: MutableList<ScoreboardLine>) {
    fun line(value: String) {
        lines += Animated.Static(value)
    }

    fun line(animated: ScoreboardLine) {
        lines += animated
    }

    fun emptyLine() = line("")

}
