package com.github.erfangc.marketmonitor.analysis.summary.models

import com.github.erfangc.marketmonitor.analysis.summary.CompanyReturnSummaryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@ExperimentalStdlibApi
@RestController
@RequestMapping("summary-analysis")
class SummaryAnalysisController(private val summaryService: CompanyReturnSummaryService) {
    @GetMapping
    fun getSummaryAnalysis(): SummaryAnalysis {
        return summaryService.getSummaryAnalysis()
    }
}