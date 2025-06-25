package dev.dani.lumina.api.util


/*
 * Project: lumina
 * Created at: 24/6/25 15:02
 * Created by: Dani-error
 */
interface PlayerEventHandler<P> {

    fun onJoin(player: P)
    fun onQuit(player: P)

}