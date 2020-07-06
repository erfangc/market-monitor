package com.github.erfangc.marketmonitor.dailymetrics

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetric
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetricRow
import com.github.erfangc.marketmonitor.io.HttpClient
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.mongodb.client.model.Indexes
import com.vhl.blackmo.grass.dsl.grass
import org.apache.http.client.methods.HttpGet
import org.litote.kmongo.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.InputStream
import java.time.LocalDate

@ExperimentalStdlibApi
@Service
class DailyMetricsService {
    private val log = LoggerFactory.getLogger(DailyMetricsService::class.java)

    private val apiKey = System.getenv("QUANDL_API_KEY")
            ?: error("environment QUANDL_API_KEY is not defined")

    companion object {
        val dailyMetrics = database.getCollection<DailyMetricRow>()
    }

    fun getDailyMetrics(date: LocalDate): List<DailyMetric> {
        return dailyMetrics
                .find(DailyMetricRow::dailyMetric / DailyMetric::date eq date)
                .map { it.dailyMetric }
                .toList()
    }

    fun getDailyMetric(ticker: String, date: LocalDate): DailyMetric? {
        return dailyMetrics.findOne(
                DailyMetricRow::dailyMetric / DailyMetric::ticker eq ticker,
                DailyMetricRow::dailyMetric / DailyMetric::date eq date
        )?.dailyMetric
    }

    fun bootstrap() {
        log.info("Bootstrapping daily metrics from Quandl and saving the results to MongoDB")
        dailyMetrics.drop()
        dailyMetrics.createIndex(Indexes.ascending((DailyMetricRow::dailyMetric / DailyMetric::date).name))
        dailyMetrics.createIndex(Indexes.hashed((DailyMetricRow::dailyMetric / DailyMetric::ticker).name))
        loadForPeriod()
    }

    fun catchUp() {
        val latestDate = dailyMetrics
                .find()
                .descendingSort(DailyMetricRow::dailyMetric / DailyMetric::date)
                .limit(1)
                .firstOrNull()?.dailyMetric?.date
        if (latestDate == null) {
            bootstrap()
        } else {
            loadForPeriod(latestDate.plusDays(1), LocalDate.now())
        }
    }

    fun loadForPeriod(startDate: LocalDate? = null, endDate: LocalDate? = null) {
        val endDate = endDate ?: LocalDate.now()
        val st = startDate ?: endDate.minusYears(1)
        st.datesUntil(endDate.plusDays(1)).forEach { date ->
            loadForDate(date)
        }
    }


    fun loadForDate(date: LocalDate) {
        val rows = csvReader().readAllWithHeader(csvInputStream(date))
        if (rows.isNotEmpty()) {
            val documents = grass<DailyMetric>().harvest(rows)
                    .map { DailyMetricRow(_id = "${it.ticker}:${it.date}", dailyMetric = it) }
            val result = dailyMetrics.insertMany(documents)
            log.info("Wrote ${result.insertedIds.size} daily metrics for $date")
        } else {
            log.info("Skipping writing daily metrics for $date since there are no results dayOfWeek=${date.dayOfWeek}")
        }
    }

    private fun csvInputStream(date: LocalDate): InputStream = HttpClient.httpClient
            .execute(HttpGet(url(date)))
            .entity
            .content

    private fun url(date: LocalDate) =
            "https://www.quandl.com/api/v3/datatables/SHARADAR/DAILY.csv?date=${date}&api_key=$apiKey"

}
