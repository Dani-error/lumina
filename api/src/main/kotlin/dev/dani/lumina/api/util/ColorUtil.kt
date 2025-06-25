package dev.dani.lumina.api.util

import java.util.regex.Pattern


/*
 * Project: lumina
 * Created at: 24/06/2025 0:07
 * Created by: Dani-error
 */
object ColorUtil {
    const val COLOR_CHAR = '§'
    private const val VALID_CODES = "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx"
    private val hexPattern = Pattern.compile("&#[A-Fa-f0-9]{6}")

    fun translateAlternateColorCodes(altColorChar: Char = '&', text: String): String {
        val chars = text.toCharArray()
        for (i in 0 until chars.size - 1) {
            if (chars[i] == altColorChar && VALID_CODES.contains(chars[i + 1])) {
                chars[i] = COLOR_CHAR
                chars[i + 1] = chars[i + 1].lowercaseChar()
            }
        }
        return String(chars)
    }

    fun color(hex: Boolean = false, input: String): String {
        var text = translateAlternateColorCodes('&', input)

        if (hex) {
            val matcher = hexPattern.matcher(text)
            while (matcher.find()) {
                try {
                    val color: String = matcher.group()
                    val hexColor = color.replace("&", "").replace("x", "#")
                    val parsedColor = fromHex(hexColor)

                    if (parsedColor != null)
                        text = text.replace(color, parsedColor)
                } catch (_: Exception) {
                    // Errors about unknown group, can be safely ignored!
                }
            }
        }
        return text
    }

    fun fromHex(color: String): String? {
        if (!(color.length == 7 && color[0] == '#')) return null

        val magic = StringBuilder("§x")
        val var3: CharArray = color.substring(1).toCharArray()
        val var4 = var3.size

        for (var5 in 0 until var4) {
            val c = var3[var5]
            magic.append('§').append(c)
        }

        return magic.toString()
    }

    fun getLastColors(input: String): String {
        var result = ""
        val length = input.length

        for (index in length - 1 downTo 0) {
            if (input[index] == COLOR_CHAR && index < length - 1) {
                val hex = getHexColor(input, index)
                if (hex != null) {
                    result = hex + result
                    break
                }

                val c = input[index + 1].lowercaseChar()
                if (VALID_CODES.contains(c)) {
                    val colorCode = "$COLOR_CHAR$c"
                    result = colorCode + result
                    if (c in "0123456789abcdefr") break
                }
            }
        }

        return result
    }


    private fun getHexColor(input: String, index: Int): String? {
        if (index < 12) return null

        if (input[index - 11] == 'x' && input[index - 12] == '§') {
            for (i in index - 10..index step 2) {
                if (input[i] != '§') return null
            }

            for (i in index - 9..index + 1 step 2) {
                val c = input[i]
                if (c < '0' || c > 'f') return null
                if (c > '9' && c < 'A') return null
                if (c > 'F' && c < 'a') return null
            }

            return input.substring(index - 12, index + 2)
        }

        return null
    }

}
