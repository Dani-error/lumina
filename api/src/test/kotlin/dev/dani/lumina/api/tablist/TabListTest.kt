package dev.dani.lumina.api.tablist

import dev.dani.lumina.api.animation.Animated
import dev.dani.lumina.api.animation.animate
import dev.dani.lumina.api.animation.currentFrame
import dev.dani.lumina.api.profile.Profile
import dev.dani.lumina.api.util.getTabCoordinates
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TablistTest {

    @Test
    fun `tablist builder should create valid animated tablist`() {
        val adapter = tablist<Any?> { _ ->
            header(animate(
                every = 30,
                "Hello, world!",
                "Goodbye!"
            ))

            footer("This is a test footer.")

            columns {
                LEFT {
                    entry(0, animate(10, "Loading.", "Loading..", "Loading...")) {
                        ping(10)
                        skin(Profile.unresolved("Steve"))
                    }
                }
            }
        }

        val tablist = adapter.create(null)

        // --- Check header frames ---
        val header = tablist.header
        assertTrue(header is Animated.Cycling)
        val headerAnim = header as Animated.Cycling
        assertEquals(2, headerAnim.frames.size)
        assertEquals(30, headerAnim.interval)

        // --- Check footer ---
        val footer = tablist.footer
        assertTrue(footer is Animated.Static)
        val footerStatic = footer as Animated.Static
        assertEquals(listOf("This is a test footer."), footerStatic.value)

        // --- Check entries ---
        assertEquals(1, tablist.entries.size)
        val entry = tablist.entries[0]

        assertEquals(TabColumn.LEFT, entry.column)
        assertEquals(0, entry.slot)
        assertTrue(entry.text is Animated.Cycling)

        val textAnim = entry.text as Animated.Cycling
        assertEquals(3, textAnim.frames.size)
        assertEquals("Loading.", textAnim.frames[0])
        assertEquals("Loading..", textAnim.frames[1])
        assertEquals("Loading...", textAnim.frames[2])
        assertEquals(10, textAnim.interval)

        // --- Animation tick test ---
        assertEquals("Loading.", entry.text.currentFrame(0))
        assertEquals("Loading..", entry.text.currentFrame(10))
        assertEquals("Loading...", entry.text.currentFrame(20))
        assertEquals("Loading.", entry.text.currentFrame(30))
    }

    @Test
    fun `tab entry coordinates`() {
        // --- Others ---
        val globalSlot = 4
        val (column, slot) = globalSlot.getTabCoordinates()
        assertEquals(column, TabColumn.LEFT)
        assertEquals(slot, 1)
    }
}
