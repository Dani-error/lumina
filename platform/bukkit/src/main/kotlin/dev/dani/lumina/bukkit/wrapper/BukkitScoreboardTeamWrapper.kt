package dev.dani.lumina.bukkit.wrapper

import dev.dani.lumina.api.wrapper.ScoreboardTeamWrapper
import org.bukkit.scoreboard.Team


/*
 * Project: lumina
 * Created at: 24/06/2025 13:19
 * Created by: Dani-error
 */
class BukkitScoreboardTeamWrapper(private val team: Team): ScoreboardTeamWrapper {
    override var prefix: String
        get() = team.prefix
        set(value) {
            team.prefix = value
        }

    override var suffix: String
        get() = team.suffix
        set(value) {
            team.suffix = value
        }

    override fun addEntry(name: String) {
        team.addEntry(name)
    }

    override fun removeEntry(name: String) {
        team.removeEntry(name)
    }

    override fun hasEntry(name: String): Boolean =
        team.hasEntry(name)

    override fun unregister() =
        team.unregister()
}