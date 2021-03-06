package com.github.erfangc.marketmonitor.dailymetrics

import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetric
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetricRow
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.github.erfangc.marketmonitor.quandl.QuandlService.exportQuandl
import com.mongodb.client.model.Indexes
import com.vhl.blackmo.grass.dsl.grass
import org.litote.kmongo.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDate

@ExperimentalStdlibApi
@Service
class DailyMetricsService {
    private val log = LoggerFactory.getLogger(DailyMetricsService::class.java)

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
        exportQuandl(publisher = "SHARADAR", table = "DAILY") { response ->
            val rows = response.asList()
            if (rows.isNotEmpty()) {
                val documents = grass<DailyMetric>().harvest(rows)
                        .map { DailyMetricRow(_id = "${it.ticker}:${it.date}", dailyMetric = it) }
                val result = dailyMetrics.insertMany(documents)
                log.info("Wrote ${result.insertedIds.size} daily metrics for $date")
            } else {
                log.info("Skipping writing daily metrics for $date since there are no results dayOfWeek=${date.dayOfWeek}")
            }
        }
    }

}
