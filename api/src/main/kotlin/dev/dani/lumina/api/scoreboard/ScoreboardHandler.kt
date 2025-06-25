package dev.dani.lumina.api.scoreboard


/*
 * Project: lumina
 * Created at: 23/06/2025 22:57
 * Created by: Dani-error
 */
interface ScoreboardHandler<P> : ScoreboardHandlerSettings<P> {

    val layoutMapping: MutableMap<P, ScoreboardLayout<P>>

    fun tick(tick: Int)
    fun stop()

}

interface ScoreboardHandlerSettings<P> {
    var updateTicks: Int
    var adapter:ScoreboardAdapterFactory<P>?

    class Builder<P> {
        var adapter: ScoreboardAdapterFactory<P>? = null
        var updateTicks: Int = 1

        fun build(): ScoreboardHandlerSettings<P> = object : ScoreboardHandlerSettings<P> {
            override var adapter = this@Builder.adapter
            override var updateTicks = this@Builder.updateTicks
        }
    }
}