package dev.dani.lumina.api.scoreboard

import dev.dani.lumina.api.util.TickableThread


/*
 * Project: lumina
 * Created at: 24/06/2025 23:02
 * Created by: Dani-error
 */
class ScoreboardThread<P>(private val handler: ScoreboardHandler<P>, disabled: () -> Boolean) : TickableThread("Scoreboard", disabled) {

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