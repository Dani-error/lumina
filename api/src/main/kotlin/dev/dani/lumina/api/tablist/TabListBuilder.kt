@file:Suppress("unused")

package dev.dani.lumina.api.tablist

import dev.dani.lumina.api.animation.Animated


/*
 * Project: lumina
 * Created at: 23/06/2025 13:57
 * Created by: Dani-error
 */
typealias TabLines = Animated<List<String>>


fun <P> tablist(block: TablistBuilder<P>.(P) -> Unit): TablistAdapterFactory<P> = object : TablistAdapterFactory<P> {
    override fun create(viewer: P): TablistAdapter {
        return TablistBuilder(viewer).apply { block(viewer) }.build()
    }
}

class TablistBuilder<P>(val player: P) {
    private var _header: TabLines = Animated.Static(emptyList())
    val header: TabLines get() = _header
    private var _footer: TabLines = Animated.Static(emptyList())
    val footer: TabLines get() = _footer

    fun header(line: String) {
        _header = Animated.Static(listOf(line))
    }

    fun header(lines: List<String>) {
        _header = Animated.Static(lines)
    }

    fun header(interval: Int, cycle: List<List<String>>) {
        _header = Animated.Cycling(cycle, interval)
    }

    fun header(animated: TabLines) {
        _header = animated
    }

    fun header(animated: Animated.Cycling<String>) {
        header(animated.interval, animated.frames.map { listOf(it) })
    }

    fun footer(line: String) {
        _footer = Animated.Static(listOf(line))
    }

    fun footer(lines: List<String>) {
        _footer = Animated.Static(lines)
    }

    fun footer(interval: Int, cycle: List<List<String>>) {
        _footer = Animated.Cycling(cycle, interval)
    }

    fun footer(animated: TabLines) {
        _footer = animated
    }

    fun footer(animated: Animated.Cycling<String>) {
        footer(animated.interval, animated.frames.map { listOf(it) })
    }


    private val entriesList = mutableListOf<TabEntry>()

    fun columns(block: ColumnBuilder.() -> Unit) {
        ColumnBuilder(entriesList).apply(block)
    }

    fun entries(block: EntryListBuilder.() -> Unit) {
        EntryListBuilder(entriesList).apply(block)
    }

    fun build(): TablistAdapter = object : TablistAdapter {
        override val header = _header
        override val footer = _footer
        override val entries = entriesList.toList()
    }
}
