package dev.dani.lumina.api.animation


/*
 * Project: lumina
 * Created at: 24/6/25 20:05
 * Created by: Dani-error
 */

// sealed for animated/static/dynamic values with interval support
sealed interface Animated<T> {
    data class Static<T>(val value: T) : Animated<T>
    data class Cycling<T>(val frames: List<T>, val interval: Int) : Animated<T>
    data class Dynamic<T>(val interval: Int = 1, val supplier: () -> T) : Animated<T> {
        private var lastTick: Int = -1
        private var cached: T? = null

        fun get(tick: Int): T {
            if (lastTick == -1 || (tick - lastTick) >= interval) {
                cached = supplier()
                lastTick = tick
            }
            return cached!!
        }
    }
}