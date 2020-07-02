package com.github.erfangc.marketmonitor.analysis.marketsummary

import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetric
import com.github.erfangc.marketmonitor.io.MongoDB.database
import com.github.erfangc.marketmonitor.previousWorkingDay
import com.github.erfangc.marketmonitor.tickers.TickerService
import org.litote.kmongo.findOneById
import org.litote.kmongo.getCollection
import org.litote.kmongo.save
import org.nield.kotlinstatistics.median
import org.springframework.stereotype.Service
import java.time.LocalDate

@ExperimentalStdlibApi
@Service
class MarketSummaryService(
        private val tickerService: TickerService,
        private val dailyMetricsService: DailyMetricsService
) {

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
        val date = date ?: LocalDate.now().previousWorkingDay()
        val allMetrics = dailyMetricsService.getDailyMetrics(date)

        val tickers = allMetrics.mapNotNull {
            tickerService.getTicker(it.ticker)
        }.associateBy { it.ticker }

        val positivePe = allMetrics.filter { (it.pe ?: 0.0) > 0 }
        val negativePe = allMetrics.filter { (it.pe ?: 0.0) <= 0.0 }
        val positiveMc = positivePe.sumByDouble { it.marketcap ?: 0.0 }
        val negativeMc = negativePe.sumByDouble { it.marketcap ?: 0.0 }
        val marketCapWeightedPe = positivePe.sumByDouble {
            (it.pe ?: 0.0) * ((it.marketcap ?: 0.0) / positiveMc)
        }

        val sectorSummaries = subMarketSummary(allMetrics) {
            tickers[it.ticker]?.sector ?: "N/A"
        }

        return MarketSummary(
                _id = date.toString(),
                date = date,
                totalMarketCap = positiveMc + negativeMc,
                marketCapWeightedPe = marketCapWeightedPe,
                medianPe = positivePe.mapNotNull { it.pe }.median(),
                peCount = positivePe.size,
                percentNegativeEarningMarketCapWeighted = negativeMc / (positiveMc + negativeMc),
                percentNegativeEarningUnweighted = negativePe.size / (positivePe.size.toDouble() + negativePe.size.toDouble()),
                sectorSummaries = sectorSummaries
        )
    }

    private fun subMarketSummary(
            totalMktMetrics: List<DailyMetric>,
            groupByFn: (DailyMetric) -> String
    ): List<SubMarketSummary> {
        return totalMktMetrics
                .groupBy(groupByFn)
                .map { (name, subMktMetrics) ->
                    // Sub market standalone computations
                    val subMarketWithPositivePe = subMktMetrics.filter { (it.pe ?: 0.0) > 0 }
                    val subMarketWithPositivePeMktCap = subMarketWithPositivePe.sumByDouble { it.marketcap ?: 0.0 }
                    val marketCapWeightedPe = subMarketWithPositivePe.sumByDouble {
                        (it.pe ?: 0.0) * (it.marketcap ?: 0.0) / subMarketWithPositivePeMktCap
                    }
                    val subMarketTotalMktCap = subMktMetrics.sumByDouble { it.marketcap ?: 0.0 }

                    // Contribution to total market computations
                    val totalMarketMktCap = totalMktMetrics.sumByDouble { it.marketcap ?: 0.0 }
                    val contributionToTotalMarketPe = subMarketWithPositivePe.sumByDouble {
                        (it.pe ?: 0.0) * (it.marketcap ?: 0.0) / totalMarketMktCap
                    }
                    SubMarketSummary(
                            name = name,
                            marketCap = subMarketTotalMktCap,
                            marketCapWeightedPe = marketCapWeightedPe,
                            medianPe = subMarketWithPositivePe.mapNotNull { it.pe }.median(),
                            tickerCount = subMktMetrics.size,
                            percentNegativeEarningUnweighted = subMarketWithPositivePe.size / subMktMetrics.size.toDouble(),
                            percentNegativeEarningMarketCapWeighted = 1 - subMarketWithPositivePeMktCap / subMarketTotalMktCap,
                            contributionToTotalMarketPe = contributionToTotalMarketPe
                    )
                }
    }

}