package dev.dani.lumina.api.wrapper


/*
 * Project: lumina
 * Created at: 23/06/2025 23:42
 * Created by: Dani-error
 */
interface ScoreboardTeamWrapper {

    var prefix: String
    var suffix: String

    fun addEntry(name: String)
    fun hasEntry(name: String): Boolean
    fun removeEntry(name: String)
    fun unregister()

}