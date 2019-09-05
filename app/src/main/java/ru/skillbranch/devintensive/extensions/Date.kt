package ru.skillbranch.devintensive.extensions

import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    var dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.shortFormat(): String? {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

private fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time /  DAY
    return day1 == day2
}

fun Date.add(value: Int, units: TimeUnits) : Date {
    var time = this.time
    time += when(units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }
    this.time = time
    return this
}

fun Date.humanizeDiff(): String {
    var periodDate: Pair<Date, Date>

    //    только что
    periodDate = Pair(Date().add(1, TimeUnits.SECOND), Date().add(-1, TimeUnits.SECOND))
    if (this.before(periodDate.first) && this.after(periodDate.second)) {
        return "только что"
    }

    //    несколько секунд назад
    periodDate = Pair(Date().add(45, TimeUnits.SECOND), Date().add(-45, TimeUnits.SECOND))
    if (this.before(periodDate.first) && this.after(periodDate.second)) {
        return when {
            this.before(Date()) -> "несколько секунд назад"
            else -> "через несколько секунд"
        }
    }

    //    минуту назад
    periodDate = Pair(Date().add(75, TimeUnits.SECOND), Date().add(-75, TimeUnits.SECOND))
    if (this.before(periodDate.first) && this.after(periodDate.second)) {
        return when {
            this.before(Date()) -> "минуту назад"
            else -> "через минуту"
        }
    }

    //    N минут назад
    periodDate = Pair(Date().add(45, TimeUnits.MINUTE), Date().add(-45, TimeUnits.MINUTE))
    if (this.before(periodDate.first) && this.after(periodDate.second)) {
        val duration = TimeUnit.MINUTES.convert((Date().time - this.time), TimeUnit.MILLISECONDS).toInt()
        return when {
            duration > 0 -> "${TimeUnits.MINUTE.plural(duration)} назад"
            else -> "через ${TimeUnits.MINUTE.plural(duration)}"
        }
    }

    //    час назад
    periodDate = Pair(Date().add(75, TimeUnits.MINUTE), Date().add(-75, TimeUnits.MINUTE))
    if (this.before(periodDate.first) && this.after(periodDate.second)) {
        return when {
            this.before(Date()) -> "час назад"
            else -> "через час"
        }
    }

    //    N часов назад
    periodDate = Pair(Date().add(22, TimeUnits.HOUR), Date().add(-22, TimeUnits.HOUR))
    if (this.before(periodDate.first) && this.after(periodDate.second)) {
        val duration = TimeUnit.HOURS.convert((Date().time - this.time), TimeUnit.MILLISECONDS).toInt()
        return when {
            duration > 0 -> "${TimeUnits.HOUR.plural(duration)} назад"
            else -> "через ${TimeUnits.HOUR.plural(duration)}"
        }
    }

    //    день назад
    periodDate = Pair(Date().add(26, TimeUnits.HOUR), Date().add(-26, TimeUnits.HOUR))
    if (this.before(periodDate.first) && this.after(periodDate.second)) {
        return when {
            this.before(Date()) -> "день назад"
            else -> "через день"
        }
    }

    //    N дней назад
    periodDate = Pair(Date().add(360, TimeUnits.DAY), Date().add(-360, TimeUnits.DAY))
    if (this.before(periodDate.first) && this.after(periodDate.second)) {
        val duration = TimeUnit.DAYS.convert((Date().time - this.time), TimeUnit.MILLISECONDS).toInt()
        return when {
            duration > 0 -> "${TimeUnits.DAY.plural(duration)} назад"
            else -> "через ${TimeUnits.DAY.plural(duration)}"
        }
    }

    return when {
        this.before(Date()) -> "более года назад"
        else -> "более чем через год"
    }
}

fun getPlurals(value: Int, dict: Triple<String, String, String>): String {
    return when {
        value % 100 in 5..20 -> dict.third
        value % 10 == 1 -> dict.first
        value % 10 in 2..4 -> dict.second
        else -> dict.third
    }
}

enum class TimeUnits {
    SECOND {
        override fun plural(value: Int): String {
            val count = abs(value)
            return "$count ${getPlurals(count, Triple("секунду", "секунды", "секунд"))}"
        }
    },
    MINUTE {
        override fun plural(value: Int): String {
            val count = abs(value)
            return "$count ${getPlurals(count, Triple("минуту", "минуты", "минут"))}"
        }
    },
    HOUR {
        override fun plural(value: Int): String {
            val count = abs(value)
            return "$count ${getPlurals(count, Triple("час", "часа", "часов"))}"
        }
    },
    DAY {
        override fun plural(value: Int): String {
            val count = abs(value)
            return "$count ${getPlurals(count, Triple("день", "дня", "дней"))}"
        }
    };
    abstract fun plural(value: Int): String

}