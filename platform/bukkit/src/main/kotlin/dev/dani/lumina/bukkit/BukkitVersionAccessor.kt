package dev.dani.lumina.bukkit

import dev.dani.lumina.api.platform.PlatformVersionAccessor
import io.papermc.lib.PaperLib

/*
 * Project: lumina
 * Created at: 25/05/2025 20:08
 * Created by: Dani-error
 */
object BukkitVersionAccessor {

    fun versionAccessor(): PlatformVersionAccessor =
        PaperLibPlatformVersionAccessor

    internal object PaperLibPlatformVersionAccessor : PlatformVersionAccessor {

        override val major: Int = 1
        override val minor: Int
            get() = PaperLib.getMinecraftVersion()
        override val patch: Int
            get() = PaperLib.getMinecraftPatchVersion()

        override fun atLeast(major: Int, minor: Int, patch: Int): Boolean {
            return PaperLib.isVersion(minor, patch)
        }

    }
}