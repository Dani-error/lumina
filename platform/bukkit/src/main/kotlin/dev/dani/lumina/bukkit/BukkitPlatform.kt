@file:Suppress("unused")

package dev.dani.lumina.bukkit

import dev.dani.lumina.api.platform.*
import dev.dani.lumina.api.profile.ProfileResolver
import dev.dani.lumina.api.scoreboard.ScoreboardHandler
import dev.dani.lumina.api.scoreboard.ScoreboardHandlerSettings
import dev.dani.lumina.api.wrapper.ScoreboardWrapper
import dev.dani.lumina.api.tablist.TabListHandler
import dev.dani.lumina.api.tablist.TabListHandlerSettings
import dev.dani.lumina.api.util.ClassHelper.classExists
import dev.dani.lumina.bukkit.BukkitProfileResolver.profileResolver
import dev.dani.lumina.bukkit.protocol.BukkitProtocolAdapter
import dev.dani.lumina.bukkit.wrapper.BukkitScoreboardWrapper
import dev.dani.lumina.common.platform.CommonPlatform
import dev.dani.lumina.common.platform.CommonPlatformBuilder
import dev.dani.lumina.common.scoreboard.CommonScoreboardHandler
import dev.dani.lumina.common.tablist.CommonTabListHandler
import dev.dani.lumina.common.task.AsyncPlatformTaskManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin


/*
 * Project: lumina
 * Created at: 25/05/2025 21:48
 * Created by: Dani-error
 */
class BukkitPlatform : CommonPlatformBuilder<Player, Plugin>() {

    private val FOLIA: Boolean = classExists("io.papermc.paper.threadedregions.RegionizedServerInitEvent")

    override fun prepareBuild() {
        // set the profile resolver to a native platform one if not given
        if (this.profileResolver == null) {
            this.profileResolver = profileResolver()
        }

        // set the default task manager
        if (this.taskManager == null) {
            if (FOLIA) {
                this.taskManager = AsyncPlatformTaskManager.taskManager(extension!!.name)
            } else {
                this.taskManager = BukkitPlatformTaskManager.taskManager(extension!!)
            }
        }

        // set the default version accessor
        if (this.versionAccessor == null) {
            this.versionAccessor = BukkitVersionAccessor.versionAccessor()
        }

        // set the default packet adapter
        if (this.packetAdapter == null) {
            this.packetAdapter = BukkitProtocolAdapter.packetAdapter()
        }

        // set the default logger if no logger was provided
        if (this.logger == null) {
            this.logger = PlatformLogger.fromJul(extension!!.logger)
        }
    }

    override fun doBuild(): Platform<Player, Plugin> {
        val tablistHandler = CommonTabListHandler<Player>(disabled = { return@CommonTabListHandler !extension?.isEnabled!! })

        val tablistSettings = TabListHandlerSettings.Builder<Player>()
        if (this.tablistHandlerDecorator != null) {
            tablistSettings.apply(tablistHandlerDecorator!!)
        }

        tablistHandler.applySettings(tablistSettings.build())

        val scoreboardHandler = CommonScoreboardHandler<Player>(disabled = { return@CommonScoreboardHandler !extension?.isEnabled!! })

        val scoreboardSettings = ScoreboardHandlerSettings.Builder<Player>()
        if (this.scoreboardHandlerDecorator != null) {
            scoreboardSettings.apply(scoreboardHandlerDecorator!!)
        }

        scoreboardHandler.applySettings(scoreboardSettings.build())


        // build the platform
        return PlatformBridge(
            this.debug,
            this.extension!!,
            logger!!,
            profileResolver!!,
            this.packetAdapter!!,
            versionAccessor!!,
            taskManager!!,
            tablistHandler,
            scoreboardHandler
        )
    }

    companion object {
        fun bukkitPlatformBuilder(): BukkitPlatform {
            return BukkitPlatform()
        }
    }
}

internal class PlatformBridge(debug: Boolean, extension: Plugin, logger: PlatformLogger,
                              profileResolver: ProfileResolver, packetFactory: PlatformPacketAdapter<Player, Plugin>,
                              versionAccessor: PlatformVersionAccessor, taskManager: PlatformTaskManager, tablistHandler: TabListHandler<Player>,
                              scoreboardHandler: ScoreboardHandler<Player>
) : CommonPlatform<Player, Plugin>(debug, extension, logger, profileResolver, packetFactory, taskManager, versionAccessor,
    tablistHandler, scoreboardHandler
) {

    init {
        Bukkit.getPluginManager().registerEvents(BukkitListener(this), extension)
    }

    override fun getMainScoreboard(player: Player): ScoreboardWrapper<Player> = BukkitScoreboardWrapper(player.name, Bukkit.getScoreboardManager().mainScoreboard)

    override fun getOnlinePlayersNames(): List<String> = Bukkit.getOnlinePlayers().map { it.name }

    override fun getOnlinePlayers(): List<Player> = Bukkit.getOnlinePlayers().toList()

    override fun getCurrentScoreboard(player: Player): ScoreboardWrapper<Player> = BukkitScoreboardWrapper(player.name, player.scoreboard)

    override fun getNewScoreboard(player: Player): ScoreboardWrapper<Player> = BukkitScoreboardWrapper(player.name, Bukkit.getScoreboardManager().newScoreboard)
}