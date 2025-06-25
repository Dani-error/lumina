package dev.dani.lumina.api.tablist


/*
 * Project: lumina
 * Created at: 24/6/25 20:09
 * Created by: Dani-error
 */
interface TablistAdapter {
    val header: TabLines
    val footer: TabLines
    val entries: List<TabEntry>
}

interface TablistAdapterFactory<P> {
    fun create(viewer: P): TablistAdapter
}
