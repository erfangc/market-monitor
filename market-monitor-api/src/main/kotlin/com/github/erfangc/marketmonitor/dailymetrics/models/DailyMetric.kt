package com.github.erfangc.marketmonitor.dailymetrics.models

import java.time.LocalDate
import java.util.*

data class DailyMetricRow(
        val _id: String,
        val dailyMetric: DailyMetric
)

data class DailyMetric(
        val ticker: String,
        val date: LocalDate,
        val lastupdated: LocalDate,
        val ev: Double? = null,
        val evebit: Double? = null,
        val evebitda: Double? = null,
        val marketcap: Double? = null,
        val pb: Double? = null,
        val pe: Double? = null,
        val ps: Double? = null
)
