package dev.dani.lumina.api.tablist

import dev.dani.lumina.api.util.TickableThread


/*
 * Project: lumina
 * Created at: 23/06/2025 22:49
 * Created by: Dani-error
 */
class TabListThread<P>(private val handler: TabListHandler<P>, disabled: () -> Boolean) : TickableThread("Tab List", disabled) {

    override val updateTicks: Int
        get() {
            return handler.updateTicks
        }

    override fun shouldSkipExecution(): Boolean =
        handler.adapter == null

    override fun onTick() =
        handler.tick(tick)

    override fun onStop() =
        handler.stop()

}