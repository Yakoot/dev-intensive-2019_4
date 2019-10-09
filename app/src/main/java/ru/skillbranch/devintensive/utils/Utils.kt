package ru.skillbranch.devintensive.utils

import android.content.Context
import android.util.TypedValue

val dictionary: MutableMap<String, String> = mutableMapOf(
    "а" to "a",
    "б" to "b",
    "в" to "v",
    "г" to "g",
    "д" to "d",
    "е" to "e",
    "ё" to "e",
    "ж" to "zh",
    "з" to "z",
    "и" to "i",
    "й" to "i",
    "к" to "k",
    "л" to "l",
    "м" to "m",
    "н" to "n",
    "о" to "o",
    "п" to "p",
    "р" to "r",
    "с" to "s",
    "т" to "t",
    "у" to "u",
    "ф" to "f",
    "х" to "h",
    "ц" to "c",
    "ч" to "ch",
    "ш" to "sh",
    "щ" to "sh'",
    "ъ" to "",
    "ы" to "i",
    "ь" to "",
    "э" to "e",
    "ю" to "yu",
    "я" to "ya"
)

object Utils {
    fun parseFullName(fullname: String?): Pair<String?, String?> {
        val parts: List<String>? = fullname?.trim()?.split(" ")?.filterNot { it.trim().isEmpty() }

        return Pair(parts?.getOrNull(0), parts?.getOrNull(1))
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        var initials = ""
        if (firstName.isNullOrBlank() && lastName.isNullOrBlank()) {
            return null
        }
        initials += firstName?.trim()?.get(0)?.toUpperCase() ?: ""
        initials += lastName?.trim()?.get(0)?.toUpperCase() ?: ""

        return initials

    }

    fun transliteration(payload: String, divider: String = " "): String {
        val dict : MutableMap<String, String> = dictionary
        dict[" "] = divider
        var newStr = ""
        payload.trim().forEach { letter: Char ->
            val lowerLetter = letter.toLowerCase()
            val newLetter = if (dict.containsKey(lowerLetter.toString())) dict[lowerLetter.toString()] else lowerLetter
            newStr += if (letter.isUpperCase()) newLetter.toString().capitalize() else newLetter
        }
        return newStr
    }

    fun getThemeColor(context: Context, attr: Int): Int {
        val value = TypedValue()
        context.theme.resolveAttribute(attr, value, true)
        return value.data
    }
}