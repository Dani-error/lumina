package dev.dani.lumina.api.tablist


/*
 * Project: lumina
 * Created at: 23/06/2025 22:57
 * Created by: Dani-error
 */
interface TabListHandler<P> : TabListHandlerSettings<P> {

    val layoutMapping: MutableMap<P, TabLayout<P>>

    fun tick(tick: Int)
    fun stop()

}

interface TabListHandlerSettings<P> {
    var updateTicks: Int
    var adapter: TablistAdapterFactory<P>?
    var hook: Boolean
    var ignore1_7: Boolean

    class Builder<P> {
        var adapter: TablistAdapterFactory<P>? = null
        var hook: Boolean = false
        var ignore1_7: Boolean = false
        var updateTicks: Int = 1

        fun build(): TabListHandlerSettings<P> = object : TabListHandlerSettings<P> {
            override var adapter = this@Builder.adapter
            override var hook = this@Builder.hook
            override var ignore1_7 = this@Builder.ignore1_7
            override var updateTicks = this@Builder.updateTicks
        }
    }
}