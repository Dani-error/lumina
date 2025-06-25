package dev.dani.lumina.common.tablist

import dev.dani.lumina.api.animation.currentFrame
import dev.dani.lumina.api.platform.Platform
import dev.dani.lumina.api.profile.Profile
import dev.dani.lumina.api.protocol.enums.PlayerInfoAction
import dev.dani.lumina.api.wrapper.ScoreboardWrapper
import dev.dani.lumina.api.tablist.*
import dev.dani.lumina.api.tablist.definition.DEFAULT_SKIN
import dev.dani.lumina.api.util.ColorUtil
import dev.dani.lumina.api.util.generateProfile
import dev.dani.lumina.api.util.getTabCoordinates
import dev.dani.lumina.api.util.StringUtil
import java.util.concurrent.CompletableFuture


/*
 * Project: lumina
 * Created at: 23/06/2025 23:25
 * Created by: Dani-error
 */
class CommonTabLayout<P, E>(override val player: P, private val platform: Platform<P, E>): TabLayout<P> {

    private val legacy = platform.packetFactory.isLegacy(player)
    private val hexSupport = platform.versionAccessor.atLeast(1, 16, 0)

    override val scoreboard: ScoreboardWrapper<P> = platform.getNewScoreboard(player)
    override val entryMapping: MutableList<RuntimeTabEntry> = mutableListOf()
    override var tick = 0

    private var adapter: TablistAdapter? = null
    override var footer: List<String>? = null
    override var header: List<String>? = null

    /**
     * We need to send a player list name update instantly to
     * modern clients on first join, otherwise their tablist shows
     * up with white text "null" until next update is triggered.
     */
    private var isFirstJoin = true

    init {
        scoreboard.applyTo(player)
    }

    override fun create() {
        if (adapter == null) {
            if (platform.tablistHandler.adapter != null) {
                adapter = platform.tablistHandler.adapter!!.create(player)
            } else return
        }

        val mod = 4

        val dataList = mutableListOf<RuntimeTabEntry>()

        for (index in 0..79) {
            val x: Int = index % mod
            if (x >= 3 && legacy) continue

            val y: Int = index / mod
            val i: Int = y * mod + x

            val gameProfile = generateProfile(getTeamAt(i))
            val info = RuntimeTabEntry(TabColumn.entries[x], y, gameProfile, legacy)
            entryMapping.add(info)

            dataList.add(info)
        }

        platform.packetFactory.createPlayerInfoPacket(dataList, PlayerInfoAction.ADD_PLAYER).schedule(player)

        // Add everyone to the "Tab" team
        // These aren't really used for 1.17+ except for hiding our own name
        var luminaTeam = scoreboard.getTeam("Tab")
        if (luminaTeam == null) {
            luminaTeam = scoreboard.registerNewTeam("Tab")
        }

        for (target in platform.getOnlinePlayersNames()) {
            if (luminaTeam.hasEntry(target)) continue

            luminaTeam.addEntry(target)
        }


        // Add them to their own team so that our own name doesn't show up
        for (index in 0..79) {
            val x: Int = index % mod
            if (x >= 3 && legacy) continue

            val y: Int = index / mod
            val i: Int = y * mod + x

            val displayName: String = getTeamAt(i)
            val team = "$$displayName"

            var scoreboardTeam = scoreboard.getTeam(team)
            if (scoreboardTeam == null) {
                scoreboardTeam = scoreboard.registerNewTeam(team)
                scoreboardTeam.addEntry(displayName)
            }
        }


        for (target in platform.getOnlinePlayers()) {
            val targetScoreboard = platform.getCurrentScoreboard(target)
            val team = targetScoreboard.getTeam("Tab") ?: continue
            if (team.hasEntry(scoreboard.ownerName)) continue

            team.addEntry(scoreboard.ownerName)
        }
    }

    override fun refresh(tick: Int) {
        this.tick = tick
        val tablistHandler = platform.tablistHandler

        if (adapter == null) {
            if (platform.tablistHandler.adapter != null) {
                adapter = platform.tablistHandler.adapter!!.create(player)
            } else return
        }

        try {
            val entries: List<TabEntry> = adapter!!.entries
            if (entries.isEmpty()) {
                for (i in 0..79) {
                    this.update(i, "", 0, DEFAULT_SKIN)
                }
                return
            }

            for (i in 0..79) {
                val (column, slot) = i.getTabCoordinates()
                val entry = entries.find { it.column == column && it.slot == slot }

                if (entry == null) {
                    this.update(i, "", 0, DEFAULT_SKIN)
                    continue
                }


                val x: Int = entry.column.ordinal
                val y: Int = entry.slot

                if (entry.column == TabColumn.FAR_RIGHT && legacy) continue

                val mod = 4
                val index: Int = y * mod + x

                val currentText = entry.text.currentFrame(tick)
                val currentPing = entry.ping.currentFrame(tick)
                val currentSkin = entry.skin.currentFrame(tick)

                try {
                    this.update(index, currentText, currentPing, currentSkin)
                } catch (e: Exception) {
                    platform.logger.error("[Lumina | TabList] There was an error updating Tab List for ${scoreboard.ownerName}", e)
                }
            }
        } catch (e: NullPointerException) {
            if (platform.debug) {
                platform.logger.error("[Lumina | TabList] There was an error updating Tab List for ${scoreboard.ownerName}", e)
            }
        } catch (e: Exception) {
            platform.logger.error("[Lumina | TabList] There was an error updating Tab List for ${scoreboard.ownerName}", e)
        }

        this.setHeaderAndFooter()

        if (platform.getCurrentScoreboard(player) !== scoreboard && !tablistHandler.hook) {
            scoreboard.applyTo(player)
        }
    }

    private fun getEntry(column: TabColumn, slot: Int): RuntimeTabEntry? =
        this.entryMapping.find { it.column == column && it.slot == slot }

    override fun cleanup() {
        for (index in 0..79) {
            val displayName = getTeamAt(index)
            val team = "$$displayName"

            platform.getCurrentScoreboard(player).getTeam(team)?.unregister()

            val (column, slot) = index.getTabCoordinates()
            val entry = getEntry(column, slot) ?: continue

            platform.packetFactory.createPlayerInfoPacket(entry, PlayerInfoAction.REMOVE_PLAYER).schedule(player)
        }

        if (!platform.tablistHandler.hook) {
            platform.getMainScoreboard(player).applyTo(player)
        }
    }

    /**
     * Send Header and Footer to the Client but
     * only send it if we aren't on 1.7 and below.
     */
    override fun setHeaderAndFooter() {
        if (legacy) return

        if (adapter == null) {
            if (platform.tablistHandler.adapter != null) {
                adapter = platform.tablistHandler.adapter!!.create(player)
            } else return
        }


        val currentHeader = adapter!!.header.currentFrame(tick).map { ColorUtil.color(hex = hexSupport, it) }
        val currentFooter = adapter!!.footer.currentFrame(tick).map { ColorUtil.color(hex = hexSupport, it) }

        if (header != null && header == currentHeader && footer != null && footer == currentFooter) return

        header = currentHeader
        footer = currentFooter

        platform.packetFactory.createHeaderFooterPacket(currentHeader, currentFooter).schedule(player)
    }

    /**
     * Update the text, skin and ping for the specified Tablist Entry with index
     *
     * @param index [Entry index][Integer]
     * @param text  [Entry Text][String]
     * @param ping  [Latency][Integer]
     * @param skin  [Entry Skin][Profile]
     */
    fun update(index: Int, pre: String, ping: Int, skin: Profile) {
        var text = pre
        text = ColorUtil.color(hex = hexSupport, text)
        val splitString: Array<String> = StringUtil.split(text)

        val prefix = splitString[0]
        val suffix = splitString[1]

        val displayName = getTeamAt(index)
        val team = "$$displayName"

        if (team.length > 16 || displayName.length > 16) {
            if (platform.debug) {
                platform.logger.info("[Lumina | TabList] Team Name or Display Name is longer than 16")
            }
        }

        if (prefix.length > 16 || suffix.length > 16) {
            if (platform.debug) {
                platform.logger.info("[Lumina | TabList] Prefix or Suffix is longer than 16")
            }
        }

        val (column, slot) = index.getTabCoordinates()
        val entry = getEntry(column, slot) ?: return

        var changed = false
        if (prefix != entry.prefix) {
            entry.prefix = prefix
            changed = true
        }

        if (suffix != entry.suffix) {
            entry.suffix = suffix
            changed = true
        }


        // 1.7 and below support
        if (legacy) {
            val scoreboard: ScoreboardWrapper<*> = platform.getCurrentScoreboard(player)
            var luminaTeam = scoreboard.getTeam(team)
            val teamExists = luminaTeam != null

            // This is a new entry, make it's team
            if (luminaTeam == null) {
                luminaTeam = scoreboard.registerNewTeam(team)
                luminaTeam.addEntry(displayName)
            }

            if (changed || !teamExists) {
                luminaTeam.prefix = (prefix)
                luminaTeam.suffix = (suffix.ifEmpty { "" })
            }
            this.updatePing(entry, ping)

            // 1.8 to 1.20+ support
        } else {
            // So basically updating the skin automatically causes an update
            // to the display name of the fake player, so updating below is just idiotic.

            tryResolve(skin).thenApplyAsync {

                val updated: Boolean = this.updateSkin(entry, it, text)
                this.updatePing(entry, ping)

                if (!updated && (changed || this.isFirstJoin)) {
                    this.updateDisplayName(entry, text.ifEmpty { getTeamAt(index) })

                    if (this.isFirstJoin) {
                        this.isFirstJoin = false
                    }
                }
            }
        }
    }

    private fun tryResolve(profile: Profile): CompletableFuture<Profile.Resolved> {
        if (profile.resolved) return CompletableFuture.completedFuture(profile as Profile.Resolved)

        return platform.profileResolver.resolveProfile(profile)
    }

    private fun updatePing(entry: RuntimeTabEntry, ping: Int) {
        val lastConnection: Int? = entry.lastPing
        if (lastConnection != null && lastConnection == ping) return

        entry.lastPing = ping
        platform.packetFactory.createPlayerInfoPacket(entry, PlayerInfoAction.UPDATE_LATENCY).schedule(player)
    }

    private fun updateSkin(entry: RuntimeTabEntry, skin: Profile.Resolved, text: String): Boolean {
        // Only send if changed
        val lastSkin = entry.lastSkin
        if (lastSkin != null && skin.properties == lastSkin.properties) {
            return false
        }

        entry.lastSkin = skin
        entry.displayName = text
        platform.packetFactory.createPlayerInfoPacket(entry, PlayerInfoAction.UPDATE_PROFILE).schedule(player)
        return true
    }

    private fun updateDisplayName(entry: RuntimeTabEntry, name: String) {
        entry.displayName = name
        platform.packetFactory.createPlayerInfoPacket(entry, PlayerInfoAction.UPDATE_DISPLAY_NAME).schedule(player)
    }



    companion object {
        private val tabNames: Array<String> = Array(80) { "" }

        init {
            for (i in tabNames.indices) {
                val x = i % 4
                val y = i / 4
                val name = buildString {
                    append("§0§$x")
                    if (y > 9) {
                        val yChars = y.toString().toCharArray()
                        append("§${yChars[0]}§${yChars[1]}")
                    } else {
                        append("§0§${y.toString().toCharArray()[0]}")
                    }
                    append("§r")
                }
                tabNames[i] = name
            }
        }

        fun getTeamAt(index: Int): String {
            return tabNames[index]
        }
    }
}