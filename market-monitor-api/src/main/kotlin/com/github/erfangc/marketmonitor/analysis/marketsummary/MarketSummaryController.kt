package com.github.erfangc.marketmonitor.analysis.marketsummary

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@ExperimentalStdlibApi
@RequestMapping("market-summary")
class MarketSummaryController(private val marketSummaryService: MarketSummaryService) {
    @GetMapping
    fun marketSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate? = null
    ): MarketSummary {
        return marketSummaryService.marketSummary(date)
    }
}