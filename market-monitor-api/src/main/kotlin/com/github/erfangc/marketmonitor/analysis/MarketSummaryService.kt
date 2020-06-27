package com.github.erfangc.marketmonitor.analysis

import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService.Companion.dailyMetrics
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetric
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetricRow
import com.github.erfangc.marketmonitor.io.MongoDB.database
import org.litote.kmongo.*
import org.springframework.stereotype.Service
import java.time.LocalDate

@ExperimentalStdlibApi
@Service
class MarketSummaryService {

    companion object {
        val marketSumaries = database.getCollection<MarketSummary>()
    }

    fun marketSummary(date: LocalDate): MarketSummary {
        val marketSummary = marketSumaries.findOneById(date.toString())
        if (marketSummary != null) {
            return marketSummary
        }
        val all = dailyMetrics
                .find(DailyMetricRow::dailyMetric / DailyMetric::date eq date)
                .map { it.dailyMetric }
                .toList()
        val positivePe = all.filter { (it.pe ?: 0.0) > 0 }
        val negativePe = all.filter { (it.pe ?: 0.0) <= 0.0 }
        val positiveMc = positivePe.sumByDouble { it.marketcap ?: 0.0 }
        val negativeMc = negativePe.sumByDouble { it.marketcap ?: 0.0 }
        val pe = positivePe.sumByDouble {
            (it.pe ?: 0.0) * ((it.marketcap ?: 0.0) / positiveMc)
        }
        val computedMarketSummary = MarketSummary(
                _id = date.toString(),
                date = date,
                totalMarketCap = positiveMc + negativeMc,
                pe = pe,
                peCount = positivePe.size,
                positiveNegativeRatio = positiveMc / negativeMc
        )
        marketSumaries.save(computedMarketSummary)
        return computedMarketSummary
    }
}