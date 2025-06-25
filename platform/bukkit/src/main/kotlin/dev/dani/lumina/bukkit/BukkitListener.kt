package dev.dani.lumina.bukkit

import dev.dani.lumina.common.platform.CommonPlatform
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.Plugin


/*
 * Project: lumina
 * Created at: 24/6/25 15:09
 * Created by: Dani-error
 */
class BukkitListener(private val platform: CommonPlatform<Player, Plugin>) : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    fun onJoin(event: PlayerJoinEvent) =
        platform.onJoin(event.player)

    @EventHandler(priority = EventPriority.LOWEST)
    fun onQuit(event: PlayerQuitEvent) =
        platform.onQuit(event.player)

}