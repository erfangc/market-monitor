package com.github.erfangc.marketmonitor.analysis

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.streams.toList

@ExperimentalStdlibApi
@Service
class AnalysisService(
        private val marketSummaryService: MarketSummaryService
) {

    private val log = LoggerFactory.getLogger(AnalysisService::class.java)
    private val executor = Executors.newCachedThreadPool()

    fun marketSummaries(startDate: LocalDate? = null, endDate: LocalDate = LocalDate.now()): List<MarketSummary> {
        val ret = (startDate ?: endDate.minusYears(1))
                .datesUntil(endDate)
                .toList()
                .chunked(75)
                .map { chunk ->
                    executor.submit(
                            Callable {
                                chunk.filter {
                                    it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY
                                }.map { date ->
                                    marketSummaryService.marketSummary(date)
                                }
                            }
                    )
                }.map { it.get() }
                .flatten()
        log.info("Finished computing cross sectionals")
        return ret.toList()
    }

}