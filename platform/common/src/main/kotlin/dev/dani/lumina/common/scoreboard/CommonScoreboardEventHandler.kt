package dev.dani.lumina.common.scoreboard

import dev.dani.lumina.api.platform.Platform
import dev.dani.lumina.api.util.PlayerEventHandler


/*
 * Project: lumina
 * Created at: 24/6/25 14:54
 * Created by: Dani-error
 */
class CommonScoreboardEventHandler<P, E>(private val platform: Platform<P, E>): PlayerEventHandler<P> {

    override fun onJoin(player: P) {
        val layout = CommonScoreboardLayout(player, platform)
        layout.create()
        platform.scoreboardHandler.layoutMapping[player] = layout
    }

    override fun onQuit(player: P) {
        platform.scoreboardHandler.layoutMapping.remove(player)
    }

}