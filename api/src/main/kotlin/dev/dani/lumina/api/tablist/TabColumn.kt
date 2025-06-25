@file:Suppress("unused")

package dev.dani.lumina.api.tablist

import dev.dani.lumina.api.animation.Animated


/*
 * Project: lumina
 * Created at: 24/6/25 20:08
 * Created by: Dani-error
 */
enum class TabColumn { LEFT, MIDDLE, RIGHT, FAR_RIGHT }

class ColumnBuilder(private val entries: MutableList<TabEntry>) {
    fun LEFT(block: ColumnScope.() -> Unit) = ColumnScope(TabColumn.LEFT, entries).apply(block)
    fun MIDDLE(block: ColumnScope.() -> Unit) = ColumnScope(TabColumn.MIDDLE, entries).apply(block)
    fun RIGHT(block: ColumnScope.() -> Unit) = ColumnScope(TabColumn.RIGHT, entries).apply(block)
    fun FAR_RIGHT(block: ColumnScope.() -> Unit) = ColumnScope(TabColumn.FAR_RIGHT, entries).apply(block)
}

class ColumnScope(private val column: TabColumn, private val entries: MutableList<TabEntry>) {

    fun entry(slot: Int, text: String, block: EntryConfig.() -> Unit = {}) {
        val config = EntryConfig().apply(block)
        entries += TabEntry(column, slot, Animated.Static(text), config.ping, config.skin)
    }

    fun entry(slot: Int, text: Animated<String>, block: EntryConfig.() -> Unit = {}) {
        val config = EntryConfig().apply(block)
        entries += TabEntry(column, slot, text, config.ping, config.skin)
    }
}