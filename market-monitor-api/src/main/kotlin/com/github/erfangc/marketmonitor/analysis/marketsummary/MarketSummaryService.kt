package com.github.erfangc.marketmonitor.analysis.marketsummary

import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService.Companion.dailyMetrics
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetric
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetricRow
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.github.erfangc.marketmonitor.tickers.TickerService
import org.litote.kmongo.*
import org.nield.kotlinstatistics.median
import org.springframework.stereotype.Service
import java.time.LocalDate

@ExperimentalStdlibApi
@Service
class MarketSummaryService(private val tickerService: TickerService) {

    companion object {
        val marketSumaries = database.getCollection<MarketSummary>()
    }

    fun marketSummaryAndSave(date: LocalDate?): MarketSummary {
        if (date != null) {
            val marketSummary = marketSumaries.findOneById(date.toString())
            if (marketSummary != null) {
                return marketSummary
            }
        }
        val marketSummary = marketSummary(date)
        marketSumaries.save(marketSummary)
        return marketSummary
    }

    fun marketSummary(date: LocalDate?): MarketSummary {
        val date = date ?: LocalDate.now()
        val allMetrics = dailyMetrics
                .find(DailyMetricRow::dailyMetric / DailyMetric::date eq date)
                .map { it.dailyMetric }
                .toList()

        val tickers = allMetrics.mapNotNull {
            tickerService.getTicker(it.ticker)
        }.associateBy { it.ticker }

        val positivePe = allMetrics.filter { (it.pe ?: 0.0) > 0 }
        val negativePe = allMetrics.filter { (it.pe ?: 0.0) <= 0.0 }
        val positiveMc = positivePe.sumByDouble { it.marketcap ?: 0.0 }
        val negativeMc = negativePe.sumByDouble { it.marketcap ?: 0.0 }
        val pe = positivePe.sumByDouble {
            (it.pe ?: 0.0) * ((it.marketcap ?: 0.0) / positiveMc)
        }

        val sectorSummaries = subMarketSummary(allMetrics) {
            tickers[it.ticker]?.sector ?: "N/A"
        }

        return MarketSummary(
                _id = date.toString(),
                date = date!!,
                totalMarketCap = positiveMc + negativeMc,
                pe = pe,
                peCount = positivePe.size,
                positiveNegativeRatio = positiveMc / negativeMc,
                sectorSummaries = sectorSummaries
        )
    }

    private fun subMarketSummary(allMetrics: List<DailyMetric>, groupByFn: (DailyMetric) -> String): List<SubMarketSummary> {
        return allMetrics.groupBy(groupByFn).map { (name, metrics) ->
            val positivePe = metrics.filter { (it.pe ?: 0.0) > 0 }
            val marketCapOfPositivePe = positivePe.sumByDouble { it.marketcap ?: 0.0 }
            val marketCapWeightedPe = positivePe.sumByDouble {
                (it.pe ?: 0.0) * (it.marketcap ?: 0.0) / marketCapOfPositivePe
            }
            SubMarketSummary(
                    name = name,
                    marketCap = metrics.sumByDouble { it.marketcap ?: 0.0 },
                    marketCapWeightedPe = marketCapWeightedPe,
                    medianPe = metrics.mapNotNull { it.pe }.median(),
                    tickerCount = metrics.size
            )
        }
    }

}