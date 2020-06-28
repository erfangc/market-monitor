package com.github.erfangc.marketmonitor.analysis

import com.github.erfangc.marketmonitor.analysis.marketsummary.MarketSummary
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@ExperimentalStdlibApi
@RestController
@RequestMapping("analysis")
class AnalysisController(private val analysisService: AnalysisService) {
    @GetMapping("market-summaries")
    fun marketSummaries(
            @RequestParam(required = false) startDate: LocalDate? = null,
            @RequestParam(required = false) endDate: LocalDate? = null
    ): List<MarketSummary> {
        return analysisService.marketSummaries()
    }
}
