package dev.dani.lumina.api.protocol

/*
 * Project: lumina
 * Created at: 24/06/2025 12:15
 * Created by: Dani-error
 */
fun interface OutboundPacket<P> {

    fun schedule(player: P)

}