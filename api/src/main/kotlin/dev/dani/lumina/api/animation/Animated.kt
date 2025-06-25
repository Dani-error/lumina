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

    data class Marquee(
        val text: String,
        val width: Int = 40,
        val interval: Int = 1 // ticks per frame
    ) : Animated<String> {
        private val paddedText = text + " ".repeat(width) // pad with spaces for smooth loop

        fun frameAt(tick: Int): String {
            val pos = (tick / interval) % paddedText.length
            val endPos = pos + width
            // wrap around if needed
            return if (endPos <= paddedText.length) {
                paddedText.substring(pos, endPos)
            } else {
                val part1 = paddedText.substring(pos)
                val part2 = paddedText.substring(0, endPos - paddedText.length)
                part1 + part2
            }
        }
    }

    data class PingPong(
        val text: String,
        val width: Int = 40,
        val interval: Int = 1
    ) : Animated<String> {
        private val paddedText = text + " ".repeat(width)
        private val length = paddedText.length
        private val cycle = 2 * (length - width) // forward + backward length

        fun frameAt(tick: Int): String {
            val t = (tick / interval) % cycle
            val pos = if (t < length - width) t else cycle - t
            val endPos = pos + width
            return paddedText.substring(pos, endPos)
        }
    }

    data class Typewriter(
        val text: String,
        val interval: Int = 1
    ) : Animated<String> {
        private val length = text.length

        fun frameAt(tick: Int): String {
            val pos = ((tick / interval) % (length + 1))
            return text.substring(0, pos)
        }
    }

    data class Blinking<T>(
        val value: T,
        val alternate: T,
        val interval: Int = 10
    ) : Animated<T> {
        fun frameAt(tick: Int): T {
            return if ((tick / interval) % 2 == 0) value else alternate
        }
    }

}