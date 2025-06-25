@file:Suppress("UNCHECKED_CAST")

package dev.dani.lumina.api.animation


/*
 * Project: lumina
 * Created at: 24/6/25 20:05
 * Created by: Dani-error
 */
fun <T> Animated<T>.currentFrame(tick: Int): T = when (this) {
    is Animated.Static -> value
    is Animated.Cycling -> frames[(tick / interval) % frames.size]
    is Animated.Dynamic -> get(tick)
    is Animated.Marquee -> frameAt(tick) as T
    is Animated.PingPong -> frameAt(tick) as T
    is Animated.Typewriter -> frameAt(tick) as T
    is Animated.Blinking -> frameAt(tick)
}
