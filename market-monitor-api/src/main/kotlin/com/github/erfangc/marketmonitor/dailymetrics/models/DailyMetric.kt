package com.ma.dailymetrics.models

import java.time.LocalDate

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
