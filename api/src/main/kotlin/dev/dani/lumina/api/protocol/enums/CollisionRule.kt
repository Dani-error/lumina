@file:Suppress("unused")

package dev.dani.lumina.api.protocol.enums


/*
 * Project: lumina
 * Created at: 31/05/2025 20:38
 * Created by: Dani-error
 */
enum class CollisionRule(val id: String) {
    ALWAYS("always"),
    NEVER("never"),
    PUSH_OTHER_TEAMS("pushOtherTeams"),
    PUSH_OWN_TEAM("pushOwnTeam");
}