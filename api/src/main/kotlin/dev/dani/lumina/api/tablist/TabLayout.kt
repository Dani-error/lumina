package dev.dani.lumina.api.tablist

import dev.dani.lumina.api.wrapper.ScoreboardWrapper


/*
 * Project: lumina
 * Created at: 24/06/2025 12:19
 * Created by: Dani-error
 */
interface TabLayout<P> {

    val player: P
    val scoreboard: ScoreboardWrapper<P>
    val entryMapping: MutableList<RuntimeTabEntry>
    var tick: Int

    var footer: List<String>?
    var header: List<String>?

    fun refresh(tick: Int)
    fun cleanup()
    fun create()
    fun setHeaderAndFooter()

}