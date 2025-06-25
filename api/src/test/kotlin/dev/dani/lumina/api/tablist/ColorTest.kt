package dev.dani.lumina.api.tablist

import dev.dani.lumina.api.util.ColorUtil
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test


/*
 * Project: lumina
 * Created at: 24/06/2025 0:11
 * Created by: Dani-error
 */
class ColorTest {

    @Test
    fun `translate color`() {
        val input = "&bHello, &fworld&b!"

        assertEquals("§bHello, §fworld§b!", ColorUtil.color(true, input))
    }

    @Test
    fun `last color`() {
        val input = "§bHello, §fworld§b!"

        assertEquals("§b", ColorUtil.getLastColors(input))
    }
}
