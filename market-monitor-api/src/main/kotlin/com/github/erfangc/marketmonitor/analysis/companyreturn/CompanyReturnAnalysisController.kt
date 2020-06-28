package com.github.erfangc.marketmonitor.analysis.companyreturn

import com.github.erfangc.marketmonitor.analysis.companyreturn.models.CompanyReturnAnalysis
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.CompanyReturnAnalysisRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@ExperimentalStdlibApi
@RequestMapping("company-return-analysis")
class CompanyReturnAnalysisController(
        private val companyReturnAnalysisService: CompanyReturnAnalysisService
) {
    @PostMapping
    fun companyReturnAnalysis(@RequestBody request: CompanyReturnAnalysisRequest): CompanyReturnAnalysis {
        return companyReturnAnalysisService.companyReturnAnalysis(request)
    }
}