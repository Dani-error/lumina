package dev.dani.lumina.common.scoreboard

import dev.dani.lumina.api.scoreboard.*


/*
 * Project: lumina
 * Created at: 24/06/2025 23:07
 * Created by: Dani-error
 */
class CommonScoreboardHandler<P>(disabled: () -> Boolean) : ScoreboardHandler<P> {

    private val thread = ScoreboardThread(this, disabled)

    override var updateTicks: Int = 1
    override var adapter: ScoreboardAdapterFactory<P>? = null
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

    override val layoutMapping = mutableMapOf<P, ScoreboardLayout<P>>()

    override fun tick(tick: Int) {
        layoutMapping.values.forEach { it.refresh(tick) }
    }

    override fun stop() {
        layoutMapping.values.forEach { it.cleanup() }
    }

    fun applySettings(settings: ScoreboardHandlerSettings<P>) {
        updateTicks = settings.updateTicks
        adapter = settings.adapter
    }

}