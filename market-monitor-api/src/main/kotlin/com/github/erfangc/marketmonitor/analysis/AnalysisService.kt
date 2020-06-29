package com.github.erfangc.marketmonitor.analysis

import com.github.erfangc.marketmonitor.analysis.marketsummary.MarketSummary
import com.github.erfangc.marketmonitor.analysis.marketsummary.MarketSummaryService
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.streams.toList

@ExperimentalStdlibApi
@Service
class AnalysisService(private val marketSummaryService: MarketSummaryService) {

    private val executor = Executors.newCachedThreadPool()

    fun marketSummaries(startDate: LocalDate? = null, endDate: LocalDate = LocalDate.now()): List<MarketSummary> {
        val ret = (startDate ?: endDate.minusYears(1))
                .datesUntil(endDate)
                .toList()
                .chunked(75)
                .map { chunk ->
                    executor.submit(computeMarketSummariesFor(chunk))
                }.map { it.get() }
                .flatten()
        return ret.toList()
    }

    private fun computeMarketSummariesFor(chunk: List<LocalDate>): Callable<List<MarketSummary>> {
        return Callable {
            chunk.filter {
                it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY
            }.map { date ->
                marketSummaryService.marketSummaryAndSave(date)
            }
        }
    }

}