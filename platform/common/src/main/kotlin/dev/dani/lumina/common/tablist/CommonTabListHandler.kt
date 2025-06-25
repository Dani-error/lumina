package dev.dani.lumina.common.tablist

import dev.dani.lumina.api.tablist.*


/*
 * Project: lumina
 * Created at: 23/06/2025 23:01
 * Created by: Dani-error
 */
class CommonTabListHandler<P>(disabled: () -> Boolean) : TabListHandler<P> {

    private val thread = TabListThread(this, disabled)

    override var updateTicks: Int = 1
    override var ignore1_7: Boolean = false
    override var adapter: TablistAdapterFactory<P>? = null
        set(value) {
            if (value == null) {
                thread.terminate()
                return
            }

            if (!thread.running) {
                thread.start()
            }

            field = value
        }
    override var hook: Boolean = false

    override val layoutMapping = mutableMapOf<P, TabLayout<P>>()

    override fun tick(tick: Int) {
        layoutMapping.values.forEach { it.refresh(tick) }
    }

    override fun stop() {
        layoutMapping.values.forEach { it.cleanup() }
    }

    fun applySettings(settings: TabListHandlerSettings<P>) {
        updateTicks = settings.updateTicks
        adapter = settings.adapter
        hook = settings.hook
        ignore1_7 = settings.ignore1_7
    }


}