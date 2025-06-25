package dev.dani.lumina.bukkit.protocol

import dev.dani.lumina.api.platform.PlatformPacketAdapter
import dev.dani.lumina.bukkit.protocol.impl.PacketEventsPacketAdapter
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin


/*
 * Project: lumina
 * Created at: 24/06/2025 13:34
 * Created by: Dani-error
 */
object BukkitProtocolAdapter {

    fun packetAdapter(): PlatformPacketAdapter<Player, Plugin> {

        // fallback
        return packetEvents()
    }
    

    fun packetEvents(): PlatformPacketAdapter<Player, Plugin> {
        return PacketEventsPacketAdapter
    }
}