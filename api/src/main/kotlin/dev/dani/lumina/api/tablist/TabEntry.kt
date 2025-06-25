package dev.dani.lumina.api.tablist

import dev.dani.lumina.api.animation.Animated
import dev.dani.lumina.api.profile.Profile
import dev.dani.lumina.api.tablist.definition.DEFAULT_SKIN


/*
 * Project: lumina
 * Created at: 24/6/25 20:09
 * Created by: Dani-error
 */
typealias TabText = Animated<String>
typealias TabPing = Animated<Int>
typealias TabSkin = Animated<Profile>

data class TabEntry(
    val column: TabColumn,
    val slot: Int,
    val text: TabText,
    val ping: TabPing,
    val skin: TabSkin
)

data class RuntimeTabEntry(val column: TabColumn, val slot: Int, val profile: Profile.Resolved, val legacy: Boolean) {
    var prefix: String? = null
    var suffix: String? = null
    var displayName: String? = null
    var lastPing: Int? = null
    var lastSkin: Profile.Resolved? = null
}

class DynamicEntryBuilder {
    var column: TabColumn = TabColumn.MIDDLE
    var slot: Int = 0

    var text: TabText = Animated.Static("")
    var ping: TabPing = Animated.Static(0)
    var skin: TabSkin = Animated.Static(DEFAULT_SKIN)

    fun text(value: String) {
        this.text = Animated.Static(value)
    }

    fun text(value: Animated<String>) {
        this.text = value
    }

    fun ping(value: Int) {
        this.ping = Animated.Static(value)
    }

    fun ping(value: Animated<Int>) {
        this.ping = value
    }

    fun skin(value: Profile) {
        this.skin = Animated.Static(value)
    }

    fun skin(value: Animated<Profile>) {
        this.skin = value
    }

    fun build(): TabEntry = TabEntry(column, slot, text, ping, skin)
}


class EntryListBuilder(private val entries: MutableList<TabEntry>) {
    fun entry(block: DynamicEntryBuilder.() -> Unit) {
        val builder = DynamicEntryBuilder().apply(block)
        entries += builder.build()
    }
}


class EntryConfig {
    var ping: TabPing = Animated.Static(0)
    var skin: TabSkin = Animated.Static(DEFAULT_SKIN)

    fun ping(value: Int) {
        this.ping = Animated.Static(value)
    }

    fun ping(value: Animated<Int>) {
        this.ping = value
    }

    fun skin(value: Profile) {
        this.skin = Animated.Static(value)
    }

    fun skin(value: Animated<Profile>) {
        this.skin = value
    }
}