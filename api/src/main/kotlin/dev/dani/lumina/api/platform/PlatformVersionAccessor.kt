package dev.dani.lumina.api.platform


/*
 * Project: lumina
 * Created at: 23/06/2025 13:15
 * Created by: Dani-error
 */
interface PlatformVersionAccessor {

    val major: Int
    val minor: Int
    val patch: Int

    fun atLeast(major: Int, minor: Int, patch: Int): Boolean

}