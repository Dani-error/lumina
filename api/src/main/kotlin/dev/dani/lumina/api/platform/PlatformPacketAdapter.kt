package dev.dani.lumina.api.platform

import dev.dani.lumina.api.protocol.Component
import dev.dani.lumina.api.protocol.OutboundPacket
import dev.dani.lumina.api.protocol.TeamInfo
import dev.dani.lumina.api.protocol.enums.*
import dev.dani.lumina.api.tablist.RuntimeTabEntry


/*
 * Project: lumina
 * Created at: 23/06/2025 13:16
 * Created by: Dani-error
 */
interface PlatformPacketAdapter<P, E> {

    fun createHeaderFooterPacket(header: List<String>, footer: List<String>): OutboundPacket<P>

    fun createPlayerInfoPacket(entry: List<RuntimeTabEntry>, action: PlayerInfoAction): OutboundPacket<P>
    fun createPlayerInfoPacket(entry: RuntimeTabEntry, action: PlayerInfoAction): OutboundPacket<P> = createPlayerInfoPacket(listOf(entry), action)

    fun createTeamsPacket(mode: TeamMode, teamName: String, info: TeamInfo? = null, players: List<String> = emptyList()): OutboundPacket<P>

    fun createScoreUpdatePacket(entityName: String, action: UpdateScoreAction, objectiveName: String, value: Int = 0): OutboundPacket<P>

    fun createObjectivePacket(name: String, mode: ObjectiveMode, displayName: Component, renderType: ObjectiveRenderType?): OutboundPacket<P>

    fun createDisplayObjectivePacket(name: String, position: DisplaySlot): OutboundPacket<P>

    fun initialize(platform: Platform<P, E>)

    fun atLeast(player: P, major: Int, minor: Int, patch: Int): Boolean
    fun isLegacy(player: P) = !atLeast(player, 1, 8, 0)

}