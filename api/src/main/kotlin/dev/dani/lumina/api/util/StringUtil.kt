package dev.dani.lumina.api.util


/*
 * Project: lumina
 * Created at: 24/06/2025 0:08
 * Created by: Dani-error
 */
object StringUtil {

    fun split(text: String): Array<String> {
        if (text.length <= 16) {
            return arrayOf(text, "")
        }

        var prefix = text.substring(0, 16)
        var suffix: String

        if (prefix[15] == ColorUtil.COLOR_CHAR || prefix[15] == '&') {
            prefix = prefix.substring(0, 15)
            suffix = text.substring(15)
        } else if (prefix[14] == ColorUtil.COLOR_CHAR || prefix[14] == '&') {
            prefix = prefix.substring(0, 14)
            suffix = text.substring(14)
        } else {
            val lastColor = ColorUtil.getLastColors(prefix)
            suffix = lastColor + text.substring(16)
        }

        if (suffix.length > 16) {
            suffix = suffix.substring(0, 16)
        }

        return arrayOf(prefix, suffix)
    }




}