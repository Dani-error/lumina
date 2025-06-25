@file:Suppress("unused")

package dev.dani.lumina.api.scoreboard


/*
 * Project: lumina
 * Created at: 25/06/2025 0:01
 * Created by: Dani-error
 */
data class ScoreboardStyle(
    var descending: Boolean,
    var startNumber: Int
) {
    fun reverse(): ScoreboardStyle = copy(descending = !descending)

    companion object Presets {
        fun down(startNumber: Int = 15) = ScoreboardStyle(descending = true, startNumber)
        fun up(startNumber: Int = 1) = ScoreboardStyle(descending = false, startNumber)
        fun negative(startNumber: Int = -1) = ScoreboardStyle(descending = true, startNumber)
        fun custom(descending: Boolean, startNumber: Int) = ScoreboardStyle(descending, startNumber)
    }
}
