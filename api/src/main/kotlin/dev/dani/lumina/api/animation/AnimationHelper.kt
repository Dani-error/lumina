@file:Suppress("unused")

package dev.dani.lumina.api.animation

import dev.dani.lumina.api.profile.Profile
import dev.dani.lumina.api.tablist.definition.DEFAULT_SKIN


/*
 * Project: lumina
 * Created at: 24/6/25 20:05
 * Created by: Dani-error
 */
fun <T> animate(every: Int = 10, vararg frames: T): Animated.Cycling<T> = Animated.Cycling(frames.toList(), interval = every)

fun <T> dynamic(interval: Int = 1, supplier: () -> T): Animated<T> = Animated.Dynamic(interval, supplier)

fun marquee(text: String, width: Int = 40, interval: Int = 1): Animated<String> =
    Animated.Marquee(text, width, interval)

fun pingPong(text: String, width: Int = 40, interval: Int = 1): Animated<String> =
    Animated.PingPong(text, width, interval)

fun typewriter(text: String, interval: Int = 1): Animated<String> =
    Animated.Typewriter(text, interval)

fun blinking(text: String, interval: Int = 10): Animated<String> =
    Animated.Blinking(text, " ".repeat(text.length), interval)

fun blinking(skin: Profile, interval: Int = 10): Animated<Profile> =
    Animated.Blinking(skin, DEFAULT_SKIN, interval)

fun blinking(ping: Int, interval: Int = 10): Animated<Int> =
    Animated.Blinking(ping, 0, interval)