package com.ma.dailymetrics

import com.ma.io.MongoDB.dailyMetrics
import com.ma.dailymetrics.models.DailyMetric
import com.mongodb.client.model.Indexes
import java.time.LocalDate

@ExperimentalStdlibApi
fun main() {
    val endDate = LocalDate.now()
    val startDate = endDate.minusYears(1)

    dailyMetrics.drop()
    dailyMetrics.createIndex(Indexes.ascending(DailyMetric::date.name))
    dailyMetrics.createIndex(Indexes.hashed(DailyMetric::ticker.name))

    startDate.datesUntil(endDate.plusDays(1)).forEach {
        date -> forDate(date)
    }

    println("Finished loading daily metrics between $startDate and $endDate")
}