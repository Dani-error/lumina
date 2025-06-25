package dev.dani.lumina.api.animation


/*
 * Project: lumina
 * Created at: 24/6/25 20:05
 * Created by: Dani-error
 */
fun <T> animate(every: Int = 10, vararg frames: T): Animated.Cycling<T> = Animated.Cycling(frames.toList(), interval = every)

fun <T> dynamic(interval: Int = 1, supplier: () -> T): Animated<T> = Animated.Dynamic(interval, supplier)
