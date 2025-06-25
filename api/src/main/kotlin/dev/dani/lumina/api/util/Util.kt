package dev.dani.lumina.api.util

import dev.dani.lumina.api.profile.Profile
import dev.dani.lumina.api.tablist.definition.DEFAULT_SKIN
import dev.dani.lumina.api.tablist.TabColumn
import java.util.*


/*
 * Project: lumina
 * Created at: 23/06/2025 13:18
 * Created by: Dani-error
 */
inline fun <reified T> safeEquals(
    original: Any?,
    compare: Any?,
    checker: (T, T) -> Boolean
): Boolean {
    // fast null check
    if (original == null || compare == null) return original == null && compare == null

    // fast identity check
    if (original === compare) return true

    // type check
    if (original !is T || compare !is T) return false

    // apply custom checker
    return checker(original, compare)
}

fun <T> wrap(block: () -> T): () -> T = {
    try {
        block()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

/**
 * Generate a [Profile] for the specified index.
 *
 * @param name [String]
 * @return      [Profile.Resolved]
 */
fun generateProfile(name: String): Profile.Resolved =
    Profile.resolved(name, UUID.randomUUID(), DEFAULT_SKIN.properties ?: emptySet())

fun Int.getTabCoordinates(): Pair<TabColumn, Int> {
    val mod = 4
    val x = this % mod

    val y = this / mod
    val column = TabColumn.entries[x]
    return column to y
}