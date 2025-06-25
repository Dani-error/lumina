package dev.dani.lumina.api.util


/*
 * Project: lumina
 * Created at: 24/6/25 21:10
 * Created by: Dani-error
 */
abstract class TickableThread(module: String, private val disabled: () -> Boolean) : Thread() {

    @Volatile
    var running = false

    protected var tick = 0

    open val updateTicks = 1


    init {
        name = "Lumina ($module) | Thread tick"
        isDaemon = true
    }

    override fun run() {
        while (running) {
            if (shouldSkipExecution()) return

            if (disabled()) {
                this.terminate()
                return
            }

            this.tick++
            this.tick()

            try {
                sleep(updateTicks * 50L)
            } catch (e: InterruptedException) {
                //
            }
        }
    }

    fun terminate() {
        this.running = false
        this.tick = 0
        onStop()
        interrupt()
    }

    override fun start() {
        running = true
        super.start()
    }

    private fun tick() {
        if (disabled()) return

        onTick()
    }

    open fun shouldSkipExecution(): Boolean = false

    abstract fun onStop()
    abstract fun onTick()
}