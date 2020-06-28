package com.github.erfangc.marketmonitor.analysis.pecontr

import com.github.erfangc.marketmonitor.dailymetrics.DailyMetricsService.Companion.dailyMetrics
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetric
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetricRow
import com.github.erfangc.marketmonitor.tickers.TickerService
import org.litote.kmongo.div
import org.litote.kmongo.eq
import org.springframework.stereotype.Service
import java.time.LocalDate

@ExperimentalStdlibApi
@Service
class PriceToEarningContributorService(
        private val tickerService: TickerService
) {

    fun priceToEarningContributor(
            date: LocalDate?,
            sector: String? = null,
            industry: String? = null
    ): List<PriceToEarningContributor> {
        val date = date ?: LocalDate.now()
        val rows = dailyMetrics
                .find(DailyMetricRow::dailyMetric / DailyMetric::date eq date)
                .toList()
                .filter { (it.dailyMetric.pe ?: 0.0) > 0.0 }

        val totalMarketCap = rows.sumByDouble { it.dailyMetric.marketcap ?: 0.0 }

        return rows
                .map {
                    val dailyMetric = it.dailyMetric
                    val ticker = dailyMetric.ticker
                    val asset = tickerService.getTicker(ticker)
                    val marketCap = dailyMetric.marketcap ?: 0.0
                    val pe = dailyMetric.pe ?: 0.0
                    PriceToEarningContributor(
                            ticker = ticker,
                            marketCap = marketCap,
                            pe = pe,
                            peContribution = pe * (marketCap / totalMarketCap),
                            name = asset?.name ?: "N/A",
                            industry = asset?.industry ?: "N/A",
                            sector = asset?.sector ?: "N/NA"
                    )
                }
                .sortedByDescending { it.peContribution }
    }

}