package com.github.erfangc.marketmonitor.analysis.company

import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysis
import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysisRequest
import org.springframework.web.bind.annotation.*

@RestController
@ExperimentalStdlibApi
@RequestMapping("company-analysis")
class CompanyAnalysisController(
        private val companyAnalysisService: CompanyAnalysisService
) {
    @PostMapping
    fun companyReturnAnalysis(@RequestBody request: CompanyAnalysisRequest): CompanyAnalysis {
        return companyAnalysisService.companyAnalysis(request)
    }

    @GetMapping("request/{ticker}")
    fun getCompanyAnalysisRequest(@PathVariable ticker: String): CompanyAnalysisRequest {
        return companyAnalysisService.getCompanyAnalysisRequest(ticker)
    }
}