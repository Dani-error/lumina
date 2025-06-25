package dev.dani.lumina.api.protocol.enums


/*
 * Project: lumina
 * Created at: 31/05/2025 20:38
 * Created by: Dani-error
 */
enum class NameTagVisibility(val id: String) {
    ALWAYS("always"),
    NEVER("never"),
    HIDE_FOR_OTHER_TEAMS("hideForOtherTeams"),
    HIDE_FOR_OWN_TEAM("hideForOwnTeam")
}