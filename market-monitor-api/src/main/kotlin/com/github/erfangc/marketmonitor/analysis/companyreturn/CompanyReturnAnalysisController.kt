package com.github.erfangc.marketmonitor.analysis.companyreturn

import com.github.erfangc.marketmonitor.analysis.companyreturn.models.CompanyReturnAnalysis
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.CompanyReturnAnalysisRequest
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@ExperimentalStdlibApi
@RequestMapping("company-return-analysis")
class CompanyReturnAnalysisController(
        private val companyReturnAnalysisService: CompanyReturnAnalysisService
) {
    @PostMapping("_save-daily-analysis")
    fun saveDailyAnalysis(
            @RequestParam(required = false) date: LocalDate? = null,
            @RequestParam(required = false) drop: Boolean? = null
    ) {
        return companyReturnAnalysisService.performDailyAnalysis(date, drop)
    }

    @PostMapping
    fun companyReturnAnalysis(@RequestBody request: CompanyReturnAnalysisRequest): CompanyReturnAnalysis {
        return companyReturnAnalysisService.companyReturnAnalysis(request)
    }

    @GetMapping("request/{ticker}")
    fun getCompanyReturnAnalysisRequest(@PathVariable ticker: String): CompanyReturnAnalysisRequest {
        return companyReturnAnalysisService.getCompanyReturnAnalysisRequest(ticker)
    }
}