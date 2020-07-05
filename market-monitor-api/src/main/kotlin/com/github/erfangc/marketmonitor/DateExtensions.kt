package com.github.erfangc.marketmonitor

import java.time.DayOfWeek
import java.time.LocalDate

fun LocalDate.previousWorkingDay(): LocalDate {
    var date = this.minusDays(1)
    while (date.isHoliday() || date.isWeekend()) {
        date = date.minusDays(1)
    }
    return date
}

private val holidays = listOf(
        LocalDate.of(2020, 7, 3)
)

fun LocalDate.isHoliday(): Boolean {
    return holidays.contains(this)
}

fun LocalDate.isWeekend(): Boolean {
    return this.dayOfWeek == DayOfWeek.SATURDAY || this.dayOfWeek == DayOfWeek.SUNDAY
}
