package dev.dani.lumina.api.tablist.definition


/*
 * Project: lumina
 * Created at: 23/06/2025 23:08
 * Created by: Dani-error
 */
enum class TabLatency(val value: Int) {
    FIVE_BARS(149),
    FOUR_BARS(299),
    THREE_BARS(599),
    TWO_BARS(999),
    ONE_BAR(1001),
    NO_BAR(-1)
}