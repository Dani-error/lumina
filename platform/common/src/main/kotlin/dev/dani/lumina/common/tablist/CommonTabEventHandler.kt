package dev.dani.lumina.common.tablist

import dev.dani.lumina.api.platform.Platform
import dev.dani.lumina.api.util.PlayerEventHandler


/*
 * Project: lumina
 * Created at: 24/6/25 14:54
 * Created by: Dani-error
 */
class CommonTabEventHandler<P, E>(private val platform: Platform<P, E>): PlayerEventHandler<P> {

    override fun onJoin(player: P) {
        if (platform.tablistHandler.ignore1_7 && platform.packetFactory.isLegacy(player)) return

        val layout = CommonTabLayout(player, platform)

        layout.create()
        layout.setHeaderAndFooter()

        platform.tablistHandler.layoutMapping[player] = layout
    }

    override fun onQuit(player: P) {
        val scoreboard = platform.getCurrentScoreboard(player)
        val team = scoreboard.getTeam("Tab")

        if (team != null && scoreboard !== platform.getMainScoreboard(player)) {
            if (team.hasEntry(scoreboard.ownerName)) {
                team.removeEntry(scoreboard.ownerName)
            }
            team.unregister()
        }

        platform.tablistHandler.layoutMapping.remove(player)
    }

}