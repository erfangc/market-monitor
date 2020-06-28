package com.github.erfangc.marketmonitor

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

fun LocalDate.mostRecentWorkingDay(): LocalDate {
    return when (DayOfWeek.of(this.get(ChronoField.DAY_OF_WEEK))) {
        DayOfWeek.SUNDAY -> this.minus(2, ChronoUnit.DAYS)
        DayOfWeek.SATURDAY -> this.minus(1, ChronoUnit.DAYS)
        else -> this
    }
}

fun LocalDate.previousWorkingDay(): LocalDate {
    return when (DayOfWeek.of(this.get(ChronoField.DAY_OF_WEEK))) {
        DayOfWeek.MONDAY -> this.minus(3, ChronoUnit.DAYS)
        DayOfWeek.SUNDAY -> this.minus(2, ChronoUnit.DAYS)
        else -> this.minusDays(1)
    }
}

