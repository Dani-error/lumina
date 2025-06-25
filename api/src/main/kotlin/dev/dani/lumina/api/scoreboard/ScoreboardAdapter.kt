package dev.dani.lumina.api.scoreboard


/*
 * Project: lumina
 * Created at: 24/06/2025 22:56
 * Created by: Dani-error
 */
interface ScoreboardAdapter {
    val title: ScoreboardTitle
    val lines: List<ScoreboardLine>
    val style: ScoreboardStyle
}

interface ScoreboardAdapterFactory<P> {
    fun create(viewer: P): ScoreboardAdapter
}
