package dev.dani.lumina.bukkit.protocol.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerAbstract
import com.github.retrooper.packetevents.event.PacketSendEvent
import com.github.retrooper.packetevents.manager.player.PlayerManager
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.packettype.PacketType
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.TextureProperty
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.settings.PacketEventsSettings
import com.github.retrooper.packetevents.util.ColorUtil
import com.github.retrooper.packetevents.util.TimeStampMode
import com.github.retrooper.packetevents.util.adventure.AdventureSerializer
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.*
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.Action
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfo.PlayerData
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.PlayerInfo
import dev.dani.lumina.api.platform.Platform
import dev.dani.lumina.api.platform.PlatformPacketAdapter
import dev.dani.lumina.api.profile.Profile
import dev.dani.lumina.api.protocol.Component
import dev.dani.lumina.api.protocol.OutboundPacket
import dev.dani.lumina.api.protocol.TeamInfo
import dev.dani.lumina.api.protocol.enums.*
import dev.dani.lumina.api.tablist.RuntimeTabEntry
import dev.dani.lumina.api.tablist.definition.TabLatency
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.scoreboard.RenderType
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import java.util.Optional


/*
 * Project: lumina
 * Created at: 24/06/2025 13:35
 * Created by: Dani-error
 */
object PacketEventsPacketAdapter : PlatformPacketAdapter<Player, Plugin> {

    private val PACKET_EVENTS_SETTINGS: PacketEventsSettings = PacketEventsSettings()
        .debug(false)
        .checkForUpdates(false)
        .reEncodeByDefault(false)
        .timeStampMode(TimeStampMode.NONE)

    private var serverVersion: ServerVersion? = null
    private var packetPlayerManager: PlayerManager? = null

    override fun createHeaderFooterPacket(header: List<String>, footer: List<String>): OutboundPacket<Player> = OutboundPacket { player ->
        val wrapper = WrapperPlayServerPlayerListHeaderAndFooter(
            AdventureSerializer.fromLegacyFormat(header.joinToString(separator = "\n")),
            AdventureSerializer.fromLegacyFormat(footer.joinToString(separator = "\n"))
        )

        packetPlayerManager!!.sendPacketSilently(player, wrapper)
    }

    override fun createPlayerInfoPacket(
        entry: List<RuntimeTabEntry>,
        action: PlayerInfoAction
    ): OutboundPacket<Player> = OutboundPacket { player ->
        when(action) {
            PlayerInfoAction.REMOVE_PLAYER -> {
                val playerInfoRemove = WrapperPlayServerPlayerInfoRemove(entry.map { it.profile.uniqueId })

                packetPlayerManager!!.sendPacket(player, playerInfoRemove)
            }

            PlayerInfoAction.UPDATE_LATENCY -> {
                val isClientNew: Boolean = serverVersion!!.isNewerThanOrEquals(ServerVersion.V_1_19_3)
                val playerInfo: PacketWrapper<*> = if (isClientNew) {
                    WrapperPlayServerPlayerInfoUpdate(
                        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LATENCY,
                        entry.map { PlayerInfo(convertProfile(it.profile), true, it.lastPing ?: TabLatency.FIVE_BARS.value, GameMode.SURVIVAL, null, null) }
                    )
                } else {
                    WrapperPlayServerPlayerInfo(
                        WrapperPlayServerPlayerInfo.Action.UPDATE_LATENCY,
                        entry.map { PlayerData(null, convertProfile(it.profile), GameMode.SURVIVAL, it.lastPing ?: TabLatency.FIVE_BARS.value) }
                    )
                }

                packetPlayerManager!!.sendPacket(player, playerInfo)
            }

            PlayerInfoAction.UPDATE_DISPLAY_NAME -> {

                val wrapper = if (serverVersion!!.isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
                    WrapperPlayServerPlayerInfoUpdate(
                        WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                        entry.map { PlayerInfo(
                            convertProfile(it.profile),
                            true,
                            0,
                            null,
                            AdventureSerializer.fromLegacyFormat(it.displayName),
                            null
                        ) }
                    )
                } else {
                    WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.UPDATE_DISPLAY_NAME, entry.map {
                        PlayerData(
                            AdventureSerializer.fromLegacyFormat(it.displayName),
                            convertProfile(it.profile),
                            null,
                            0
                        )
                    })
                }

                packetPlayerManager!!.sendPacket(player, wrapper)
            }

            PlayerInfoAction.ADD_PLAYER -> {
                if (serverVersion!!.isNewerThanOrEquals(ServerVersion.V_1_19_3)) {
                    val dataList = entry.map { PlayerInfo(
                        convertProfile(it.profile),
                        true,
                        0,
                        GameMode.SURVIVAL,
                        if (it.legacy) AdventureSerializer.fromLegacyFormat(it.profile.name) else null,
                        null) }

                    packetPlayerManager!!.sendPacket(player, WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER, dataList))

                    if (!isLegacy(player)) {
                        packetPlayerManager!!.sendPacket(player, WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME, dataList))
                    }

                    packetPlayerManager!!.sendPacket(player, WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED, dataList))
                    packetPlayerManager!!.sendPacket(player, WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_GAME_MODE, dataList))
                } else {
                    val dataList = entry.map { PlayerData(
                        if (!isLegacy(player)) AdventureSerializer.fromLegacyFormat(it.profile.name) else null,
                        convertProfile(it.profile),
                        GameMode.SURVIVAL,
                        0) }

                    packetPlayerManager!!.sendPacket(player, WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, dataList ))
                }
            }

            PlayerInfoAction.UPDATE_PROFILE -> {
                val isServerNew: Boolean = serverVersion!!.isNewerThanOrEquals(ServerVersion.V_1_19_3)
                val modern = atLeast(player, 1, 16, 0)
                val legacy = isLegacy(player)

                val playerInfoRemove = if (isServerNew) {
                    WrapperPlayServerPlayerInfoRemove(entry.map { it.profile.uniqueId })
                } else {
                    WrapperPlayServerPlayerInfo(
                        WrapperPlayServerPlayerInfo.Action.REMOVE_PLAYER,
                        entry.map {
                            PlayerData(null, convertProfile(it.profile), GameMode.SURVIVAL, it.lastPing ?: 0)
                        }
                    )
                }

                if (isServerNew) {
                    val data = entry.map { PlayerInfo(
                        convertProfile(it.profile.withProperties((it.lastSkin ?: it.profile).properties ?: setOf())),
                        true,
                        it.lastPing ?: TabLatency.FIVE_BARS.value,
                        GameMode.SURVIVAL,
                        if (!legacy) AdventureSerializer.fromLegacyFormat(it.displayName) else null,
                        null
                    ) }

                    val add =
                        WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER, data)
                    val list =
                        WrapperPlayServerPlayerInfoUpdate(WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_LISTED, data)

                    if (modern) {
                        packetPlayerManager!!.sendPacket(player, playerInfoRemove)
                    }

                    packetPlayerManager!!.sendPacket(player, add)
                    packetPlayerManager!!.sendPacket(player, list)

                    if (!legacy) {
                        val display = WrapperPlayServerPlayerInfoUpdate(
                            WrapperPlayServerPlayerInfoUpdate.Action.UPDATE_DISPLAY_NAME,
                            data
                        )
                        packetPlayerManager!!.sendPacket(player, display)
                    }
                } else {
                    val playerInfoAdd =
                        WrapperPlayServerPlayerInfo(WrapperPlayServerPlayerInfo.Action.ADD_PLAYER, entry.map {
                            PlayerData(
                                if (!legacy) AdventureSerializer.fromLegacyFormat(it.displayName) else null,
                                convertProfile(it.profile.withProperties((it.lastSkin ?: it.profile).properties ?: setOf())),
                                GameMode.SURVIVAL,
                                it.lastPing ?: TabLatency.FIVE_BARS.value
                            )
                        })

                    if (modern) {
                        packetPlayerManager!!.sendPacket(player, playerInfoRemove)
                    }

                    packetPlayerManager!!.sendPacket(player, playerInfoAdd)
                }
            }
        }
    }

    override fun createTeamsPacket(mode: TeamMode, teamName: String, info: TeamInfo?, players: List<String>): OutboundPacket<Player> {
        return OutboundPacket { player: Player ->
            val wrapper: PacketWrapper<*> = WrapperPlayServerTeams(
                teamName,
                WrapperPlayServerTeams.TeamMode.entries[mode.ordinal],
                if (info != null) WrapperPlayServerTeams.ScoreBoardTeamInfo(
                    convertComponent(info.displayName),
                    convertComponent(info.prefix),
                    convertComponent(info.suffix),
                    WrapperPlayServerTeams.NameTagVisibility.fromID(info.tagVisibility.id),
                    WrapperPlayServerTeams.CollisionRule.fromID(info.collisionRule.id),
                    ColorUtil.fromId(info.color.ordinal),
                    WrapperPlayServerTeams.OptionData.fromValue(info.optionData.ordinal.toByte())
                ) else null,
                players
            )

            // send the packet without notifying any listeners
            packetPlayerManager!!.sendPacketSilently(player, wrapper)
        }
    }

    override fun createScoreUpdatePacket(
        entityName: String,
        action: UpdateScoreAction,
        objectiveName: String,
        value: Int
    ): OutboundPacket<Player> = OutboundPacket { player ->
        val wrapper = WrapperPlayServerUpdateScore(entityName, WrapperPlayServerUpdateScore.Action.valueOf(action.name), objectiveName, Optional.of(value))

        packetPlayerManager!!.sendPacketSilently(player, wrapper)

    }


    override fun createObjectivePacket(
        name: String,
        mode: ObjectiveMode,
        displayName: Component,
        renderType: ObjectiveRenderType?
    ): OutboundPacket<Player> = OutboundPacket { player ->
        val wrapper = WrapperPlayServerScoreboardObjective(name, WrapperPlayServerScoreboardObjective.ObjectiveMode.valueOf(mode.name), convertComponent(displayName), if (renderType != null) WrapperPlayServerScoreboardObjective.RenderType.valueOf(renderType.name) else null)

        packetPlayerManager!!.sendPacketSilently(player, wrapper)
    }

    override fun createDisplayObjectivePacket(name: String, position: DisplaySlot): OutboundPacket<Player> = OutboundPacket { player ->
        packetPlayerManager!!.sendPacketSilently(player, WrapperPlayServerDisplayScoreboard(position.ordinal, name))
    }

    private fun convertComponent(component: Component): net.kyori.adventure.text.Component {
        return if (serverVersion!!.isNewerThanOrEquals(ServerVersion.V_1_13)) {
            component.rawMessage?.let { raw -> AdventureSerializer.fromLegacyFormat(raw) }
                ?: AdventureSerializer.parseComponent(component.encodedJsonMessage)
        } else {
            val rawMessage = requireNotNull(component.rawMessage) {
                "Versions older than 1.13 don't support json component"
            }

            return AdventureSerializer.fromLegacyFormat(rawMessage)
        }
    }

    private fun convertProfile(profile: Profile): UserProfile {
        val userProfile = UserProfile(profile.uniqueId, profile.name)
        for (property in profile.properties!!) {
            val textureProperty =
                TextureProperty(property.name, property.value, property.signature)
            userProfile.textureProperties.add(textureProperty)
        }

        return userProfile
    }

    override fun atLeast(player: Player, major: Int, minor: Int, patch: Int): Boolean {
        val version = packetPlayerManager?.getClientVersion(player)?.releaseName ?: return false
        val parts = version.split(".").mapNotNull { it.toIntOrNull() }

        val (playerMajor, playerMinor, playerPatch) = when (parts.size) {
            3 -> Triple(parts[0], parts[1], parts[2])
            2 -> Triple(parts[0], parts[1], 0)
            1 -> Triple(parts[0], 0, 0)
            else -> return false
        }

        return when {
            playerMajor != major -> playerMajor > major
            playerMinor != minor -> playerMinor > minor
            else -> playerPatch >= patch
        }
    }

    override fun initialize(platform: Platform<Player, Plugin>) {
        // build the packet events api
        val packetEventsApi = SpigotPacketEventsBuilder.buildNoCache(
            platform.extension,
            PACKET_EVENTS_SETTINGS
        )

        // while I am not the biggest fan of that, it looks like
        // that packet events is using the instance internally everywhere
        // instead of passing the created instance around, which leaves us
        // no choice than setting it as well :/
        PacketEvents.setAPI(packetEventsApi)

        // ensure that our api instance is initialized
        packetEventsApi.init()

        // store the packet player manager & server version
        this.packetPlayerManager = packetEventsApi.playerManager
        this.serverVersion = packetEventsApi.serverManager.version

        // add the packet listener
        packetEventsApi.eventManager.registerListener(TeamsPacketListener(platform))
    }

    internal class TeamsPacketListener(val platform: Platform<Player, Plugin>) :
        PacketListenerAbstract() {

        override fun onPacketSend(event: PacketSendEvent) {
            if (event.packetType !== PacketType.Play.Server.PLAYER_INFO && event.packetType !== PacketType.Play.Server.PLAYER_INFO_UPDATE && event.packetType !== PacketType.Play.Server.TEAMS) {
                return
            }

            val isClientNew = serverVersion!!.isNewerThanOrEquals(ServerVersion.V_1_19_3)

            val player = event.getPlayer() as Player?
            val tablistHandler = platform.tablistHandler
            if (player == null || (tablistHandler.ignore1_7 && isLegacy(player))) return

            if (isClientNew && event.packetType === PacketType.Play.Server.PLAYER_INFO_UPDATE) {
                val infoUpdate = WrapperPlayServerPlayerInfoUpdate(event)

                val action = infoUpdate.actions
                if (!action.contains(WrapperPlayServerPlayerInfoUpdate.Action.ADD_PLAYER)) return

                for (info in infoUpdate.entries) {
                    val userProfile = info.gameProfile ?: continue

                    this.preventGlitch(player, userProfile)
                }
            } else if (event.packetType === PacketType.Play.Server.PLAYER_INFO) {
                val infoPacket = WrapperPlayServerPlayerInfo(event)
                val action = infoPacket.action
                if (action != WrapperPlayServerPlayerInfo.Action.ADD_PLAYER) return

                for (data in infoPacket.playerDataList) {
                    val userProfile = data.userProfile ?: continue

                    this.preventGlitch(player, userProfile)
                }
            }
        }

        /**
         * Prevents our tablist from glitching out and breaking
         *
         * @param player      [Player] Player
         * @param userProfile [UserProfile] Profile
         */
        private fun preventGlitch(player: Player?, userProfile: UserProfile) {
            if (player == null) return

            val online = Bukkit.getPlayer(userProfile.uuid) ?: return

            val scoreboard: Scoreboard = player.scoreboard
            var team: Team? = scoreboard.getTeam("Tab")

            if (team == null) {
                team = scoreboard.registerNewTeam("Tab")
            }

            if (!team.hasEntry(online.name)) {
                team.addEntry(online.name)
            }
        }
    }
}