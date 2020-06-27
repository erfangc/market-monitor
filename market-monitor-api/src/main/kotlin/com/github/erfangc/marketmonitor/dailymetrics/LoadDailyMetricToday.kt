package com.ma.dailymetrics

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.ma.io.HttpClient.httpClient
import com.ma.io.MongoDB.dailyMetrics
import com.ma.dailymetrics.models.DailyMetric
import com.vhl.blackmo.grass.dsl.grass
import org.apache.http.client.methods.HttpGet
import java.io.InputStream
import java.lang.System.getenv
import java.time.LocalDate

private val apiKey = getenv("QUANDL_API_KEY")
        ?: error("environment QUANDL_API_KEY is not defined")

@ExperimentalStdlibApi
fun forToday() = forDate(LocalDate.now())

@ExperimentalStdlibApi
fun forDate(date: LocalDate) {
    val rows = csvReader().readAllWithHeader(csvInputStream(date))
    if (rows.isNotEmpty()) {
        val result = dailyMetrics.insertMany(grass<DailyMetric>().harvest(rows))
        println("Wrote ${result.insertedIds.size} daily metrics for $date")
    } else {
        println("Skipping writing daily metrics for $date since there are no results dayOfWeek=${date.dayOfWeek}")
    }
}

fun csvInputStream(date: LocalDate): InputStream = httpClient
        .execute(HttpGet(url(date)))
        .entity
        .content

private fun url(date: LocalDate) =
        "https://www.quandl.com/api/v3/datatables/SHARADAR/DAILY.csv?date=${date}&api_key=$apiKey"
