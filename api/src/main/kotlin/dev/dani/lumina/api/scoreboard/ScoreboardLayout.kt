package dev.dani.lumina.api.scoreboard

import dev.dani.lumina.api.wrapper.ScoreboardWrapper


/*
 * Project: lumina
 * Created at: 24/06/2025 22:59
 * Created by: Dani-error
 */
interface ScoreboardLayout<P> {

    val player: P
    val scoreboard: ScoreboardWrapper<P>
    var tick: Int

    fun create()
    fun refresh(tick: Int)
    fun cleanup()

}