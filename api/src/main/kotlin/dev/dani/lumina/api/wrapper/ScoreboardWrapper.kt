package dev.dani.lumina.api.wrapper


/*
 * Project: lumina
 * Created at: 23/06/2025 23:40
 * Created by: Dani-error
 */
interface ScoreboardWrapper<P> {

    val ownerName: String

    fun registerNewTeam(team: String): ScoreboardTeamWrapper
    fun getTeam(team: String): ScoreboardTeamWrapper?
    fun applyTo(player: P)

}