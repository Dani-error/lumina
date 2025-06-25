package dev.dani.lumina.common.platform

import dev.dani.lumina.api.platform.*
import dev.dani.lumina.api.profile.ProfileResolver
import dev.dani.lumina.api.scoreboard.ScoreboardHandler
import dev.dani.lumina.api.tablist.TabListHandler
import dev.dani.lumina.common.scoreboard.CommonScoreboardEventHandler
import dev.dani.lumina.common.tablist.CommonTabEventHandler


/*
 * Project: lumina
 * Created at: 24/06/2025 12:11
 * Created by: Dani-error
 */
abstract class CommonPlatform<P, E>(
    override val debug: Boolean,
    override val extension: E,
    override val logger: PlatformLogger,
    override val profileResolver: ProfileResolver,
    override val packetFactory: PlatformPacketAdapter<P, E>,
    override val taskManager: PlatformTaskManager,
    override val versionAccessor: PlatformVersionAccessor,
    override val tablistHandler: TabListHandler<P>,
    override val scoreboardHandler: ScoreboardHandler<P>
): Platform<P, E> {

    private val tablistEventHandler: CommonTabEventHandler<P, E> by lazy {
        CommonTabEventHandler(this)
    }

    private val scoreboardEventHandler: CommonScoreboardEventHandler<P, E> by lazy {
        CommonScoreboardEventHandler(this)
    }

    init {
        init()
    }

    private fun init() {
        // register the packet listeners
        this.packetFactory.initialize(this)
    }

    fun onJoin(player: P) {
        scoreboardEventHandler.onJoin(player)
        tablistEventHandler.onJoin(player)
    }

    fun onQuit(player: P) {
        scoreboardEventHandler.onQuit(player)
        tablistEventHandler.onQuit(player)
    }

}