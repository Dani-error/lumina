package dev.dani.lumina.common.scoreboard

import dev.dani.lumina.api.platform.Platform
import dev.dani.lumina.api.protocol.Component
import dev.dani.lumina.api.protocol.enums.DisplaySlot
import dev.dani.lumina.api.protocol.enums.ObjectiveMode
import dev.dani.lumina.api.protocol.enums.ObjectiveRenderType


/*
 * Project: lumina
 * Created at: 25/06/2025 11:51
 * Created by: Dani-error
 */
class ScoreboardObjective<P, E>(private val player: P, private val platform: Platform<P, E>, val name: String, val position: DisplaySlot, _displayName: String = "", _renderType: ObjectiveRenderType = ObjectiveRenderType.INTEGER) {

    var displayName = _displayName
        set(value) {
            field = value
            update()
        }

    var renderType = _renderType
        set(value) {
            field = value
            update()
        }

    fun create() =
        platform.packetFactory.createObjectivePacket(name, ObjectiveMode.CREATE, Component.empty(), ObjectiveRenderType.INTEGER).schedule(player)


    fun update() =
        platform.packetFactory.createObjectivePacket(name, ObjectiveMode.UPDATE, Component.ofRawMessage(displayName), renderType).schedule(player)

    fun remove() =
        platform.packetFactory.createObjectivePacket(
            name,
            ObjectiveMode.REMOVE,
            Component.empty(),
            null
        ).schedule(player)

    fun display() =
        platform.packetFactory.createDisplayObjectivePacket(name, position).schedule(player)

}