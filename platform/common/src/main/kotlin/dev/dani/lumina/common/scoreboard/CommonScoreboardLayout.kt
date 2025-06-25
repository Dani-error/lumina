package dev.dani.lumina.common.scoreboard

import dev.dani.lumina.api.animation.currentFrame
import dev.dani.lumina.api.platform.Platform
import dev.dani.lumina.api.protocol.Component
import dev.dani.lumina.api.protocol.TeamInfo
import dev.dani.lumina.api.protocol.enums.*
import dev.dani.lumina.api.scoreboard.ScoreboardAdapter
import dev.dani.lumina.api.scoreboard.ScoreboardLayout
import dev.dani.lumina.api.util.ColorUtil
import dev.dani.lumina.api.wrapper.ScoreboardWrapper


/*
 * Project: lumina
 * Created at: 24/06/2025 23:32
 * Created by: Dani-error
 */
class CommonScoreboardLayout<P, E>(override val player: P, private val platform: Platform<P, E>): ScoreboardLayout<P> {

    private val legacy = !platform.packetFactory.atLeast(player, 1, 13, 0)
    private val hexSupport = platform.versionAccessor.atLeast(1, 16, 0)
    private val aquaticUpdate = platform.versionAccessor.atLeast(1, 13, 0)

    override val scoreboard: ScoreboardWrapper<P> = assignNewScoreboard(player, platform)
    private val objective = ScoreboardObjective(player, platform, "Lumina", DisplaySlot.SIDEBAR)

    override var tick = 0

    private var adapter: ScoreboardAdapter? = null

    private val displayedScores: MutableMap<String, Int> = mutableMapOf()
    private val scorePrefixes: MutableMap<String, String> = mutableMapOf()
    private val scoreSuffixes: MutableMap<String, String> = mutableMapOf()
    private val sentTeamCreates: MutableSet<String> = mutableSetOf()
    private val recentlyUpdatedScores: MutableSet<String> = mutableSetOf()

    private var created = false


    init {
        scoreboard.applyTo(player)
    }

    override fun create() {
        objective.remove()

        platform.taskManager.scheduleDelayedSync(1) {
            objective.create()
            objective.display()
            created = true
        }
    }

    override fun refresh(tick: Int) {
        this.tick = tick

        if (!created) return

        val scoreboardHandler = platform.scoreboardHandler

        if (adapter == null) {
            if (scoreboardHandler.adapter != null) {
                adapter = scoreboardHandler.adapter!!.create(player)
            } else return
        }

        var title: String = ColorUtil.color(hex = hexSupport, adapter!!.title.currentFrame(tick))
        if (title.length > 32) title = title.substring(0, 32)

        var lines = adapter!!.lines.take(15)

        recentlyUpdatedScores.clear()

        // Update the title if needed.
        if (objective.displayName != title) {
            objective.displayName = title
        }

        val style = adapter!!.style

        // Reverse the lines because scoreboard scores are in descending order.
        if (!style.descending)
            lines = lines.reversed()

        var cache: Int = style.startNumber

        for (index in lines.indices) {
            val line: String = lines[index].currentFrame(tick)

            val nextValue = (index + 1)
            val displayValue = if (style.descending) cache-- else cache++

            val attributes: Array<String> =
                splitText(ColorUtil.color(hexSupport, line), nextValue)

            val prefix = attributes[0]
            val score = attributes[1]
            val suffix = attributes[2]

            recentlyUpdatedScores.add(score)

            if (!sentTeamCreates.contains(score)) this.createAndAddMember(score)


            if (!displayedScores.containsKey(score) || displayedScores[score] != displayValue) this.setScore(
                score,
                displayValue
            )


            if (!scorePrefixes.containsKey(score) || (scorePrefixes[score]) != prefix || (scoreSuffixes[score]) != suffix) this.updateScore(
                score,
                prefix,
                suffix
            )
        }


        for (displayedScore in displayedScores.keys.toSet()) {
            if (recentlyUpdatedScores.contains(displayedScore)) continue

            removeScore(displayedScore)
        }
    }

    override fun cleanup() {
        objective.remove()
    }

    // This is here so that the score joins itself, this way
    // #updateScore will work as it should (that works on a 'player'), which technically we are adding to ourselves
    private fun createAndAddMember(teamName: String) {
        platform.packetFactory.createTeamsPacket(TeamMode.CREATE, teamName, TeamInfo(
            prefix = Component.ofRawMessage("_"),
            suffix = Component.ofRawMessage("_")
        ), listOf(teamName)).schedule(player)

        sentTeamCreates.add(teamName)
    }

    private fun setScore(teamName: String, value: Int) {
        platform.packetFactory.createScoreUpdatePacket(teamName, UpdateScoreAction.CREATE_OR_UPDATE_ITEM, objective.name, value).schedule(player)

        displayedScores[teamName] = value
    }

    private fun removeScore(teamName: String) {
        platform.packetFactory.createScoreUpdatePacket(teamName, UpdateScoreAction.REMOVE_ITEM, "", 0).schedule(player)

        displayedScores.remove(teamName)
        scorePrefixes.remove(teamName)
        scoreSuffixes.remove(teamName)
    }

    private fun updateScore(teamName: String, prefix: String, suffix: String) {
        platform.packetFactory.createTeamsPacket(TeamMode.UPDATE, teamName, TeamInfo(
            prefix = Component.ofRawMessage(prefix),
            suffix = Component.ofRawMessage(suffix)
        )).schedule(player)

        scorePrefixes[teamName] = prefix
        scoreSuffixes[teamName] = suffix
    }


    companion object {

        private fun <P, E> assignNewScoreboard(player: P, platform: Platform<P, E>): ScoreboardWrapper<P> {
            val current = platform.getCurrentScoreboard(player)
            return if (current == platform.getMainScoreboard(player)) platform.getNewScoreboard(player) else current
        }

    }

    /**
     * Split the text to display on the scoreboard
     *
     * @param text  the text to split
     * @param value the value to get by score
     * @return the split text
     */
    private fun splitText(text: String, value: Int): Array<String> {
        var prefix: String
        var team = colorCharAt(value)
        var suffix = ""

        prefix = text

        if (!legacy && aquaticUpdate) {
            if (!hexSupport) {
                if (prefix.length > 64) {
                    prefix = text.substring(0, 64)

                    when (ColorUtil.COLOR_CHAR) {
                        prefix[63] -> {
                            prefix = prefix.substring(0, 63)
                            team = colorCharAt(value) + lastColors(prefix) + text.substring(63)
                        }
                        prefix[62] -> {
                            prefix = prefix.substring(0, 62)
                            team = colorCharAt(value) + lastColors(prefix) + text.substring(62)
                        }
                        else -> {
                            team = colorCharAt(value) + lastColors(prefix) + text.substring(64)
                        }
                    }

                    if (team.length > 16) {
                        team = team.substring(0, 16)

                        val start =
                            prefix.length + (team.length - colorCharAt(value).length - lastColors(prefix).length)
                        suffix = lastColors(team) + text.substring(start)

                        if (suffix.length > 16) {
                            suffix = suffix.substring(0, 16)
                        }
                    }
                }

                return arrayOf(prefix, team, suffix)
            }
            return arrayOf("", colorCharAt(value), text)
        }


        if (prefix.length > 16) {
            prefix = text.substring(0, 16)

            when (ColorUtil.COLOR_CHAR) {
                prefix[15] -> {
                    prefix = prefix.substring(0, 15)
                    team = colorCharAt(value) + lastColors(prefix) + text.substring(15)
                }
                prefix[14] -> {
                    prefix = prefix.substring(0, 14)
                    team = colorCharAt(value) + lastColors(prefix) + text.substring(14)
                }
                else -> {
                    team = colorCharAt(value) + lastColors(prefix) + text.substring(16)
                }
            }

            if (team.length > 16) {
                team = team.substring(0, 16)


                val start = prefix.length + (team.length - colorCharAt(value).length - lastColors(prefix).length)
                suffix = lastColors(team) + text.substring(start)

                if (suffix.length > 16) {
                    suffix = suffix.substring(0, 16)
                }
            }
        }

        return arrayOf(prefix, team, suffix)
    }

    /**
     * Get char format of color id
     *
     * @param colorId the color id
     * @return the color char
     */
    private fun colorCharAt(colorId: Int): String {
        return ColorUtil.COLOR_CHAR + (colorId / 10).toString() + ColorUtil.COLOR_CHAR + colorId % 10
    }

    /**
     * Get the last color of the text
     *
     * @param text to get the last color
     * @return the last char color
     */
    private fun lastColors(text: String): String {
        val lastColors: String = ColorUtil.getLastColors(text)

        if (lastColors.isNotEmpty()) return lastColors

        return ColorUtil.COLOR_CHAR + "r"
    }

}