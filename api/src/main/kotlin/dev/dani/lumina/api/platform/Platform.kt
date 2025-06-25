package dev.dani.lumina.api.platform

import dev.dani.lumina.api.tablist.TabListHandler
import dev.dani.lumina.api.profile.ProfileResolver
import dev.dani.lumina.api.scoreboard.ScoreboardHandler
import dev.dani.lumina.api.scoreboard.ScoreboardHandlerSettings
import dev.dani.lumina.api.wrapper.ScoreboardWrapper
import dev.dani.lumina.api.tablist.TabListHandlerSettings


/*
 * Project: lumina
 * Created at: 23/06/2025 13:06
 * Created by: Dani-error
 */
interface Platform<P, E> {

    val debug: Boolean
    val extension: E
    val logger: PlatformLogger
    val profileResolver: ProfileResolver
    val packetFactory: PlatformPacketAdapter<P, E>
    val versionAccessor: PlatformVersionAccessor
    val taskManager: PlatformTaskManager
    val tablistHandler: TabListHandler<P>
    val scoreboardHandler: ScoreboardHandler<P>

    fun getNewScoreboard(player: P): ScoreboardWrapper<P>
    fun getCurrentScoreboard(player: P): ScoreboardWrapper<P>
    fun getMainScoreboard(player: P): ScoreboardWrapper<P>

    fun getOnlinePlayersNames(): List<String>
    fun getOnlinePlayers(): List<P>

    interface Builder<P, E> {
        fun debug(debug: Boolean): Builder<P, E>

        fun extension(extension: E): Builder<P, E>

        fun logger(logger: PlatformLogger): Builder<P, E>

        fun profileResolver(profileResolver: ProfileResolver): Builder<P, E>

        fun versionAccessor(versionAccessor: PlatformVersionAccessor): Builder<P, E>

        fun packetFactory(packetFactory: PlatformPacketAdapter<P, E>): Builder<P, E>

        fun taskManager(taskManager: PlatformTaskManager): Builder<P, E>

        fun tablistHandler(decorator: TabListHandlerSettings.Builder<P>.() -> Unit): Builder<P, E>

        fun scoreboardHandler(decorator: ScoreboardHandlerSettings.Builder<P>.() -> Unit): Builder<P, E>

        fun build(): Platform<P, E>
    }

}