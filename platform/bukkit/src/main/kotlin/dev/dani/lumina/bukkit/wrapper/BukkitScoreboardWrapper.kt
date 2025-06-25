package dev.dani.lumina.bukkit.wrapper

import dev.dani.lumina.api.wrapper.ScoreboardWrapper
import dev.dani.lumina.api.wrapper.ScoreboardTeamWrapper
import org.bukkit.entity.Player


/*
 * Project: lumina
 * Created at: 24/06/2025 13:19
 * Created by: Dani-error
 */
class BukkitScoreboardWrapper(override val ownerName: String, private val scoreboard: org.bukkit.scoreboard.Scoreboard) : ScoreboardWrapper<Player> {

    override fun registerNewTeam(team: String): ScoreboardTeamWrapper = BukkitScoreboardTeamWrapper(scoreboard.registerNewTeam(team))

    override fun getTeam(team: String): ScoreboardTeamWrapper? {
        val bukkitTeam = scoreboard.getTeam(team) ?: return null

        return BukkitScoreboardTeamWrapper(bukkitTeam)
    }

    override fun applyTo(player: Player) {
        player.scoreboard = scoreboard
    }

}