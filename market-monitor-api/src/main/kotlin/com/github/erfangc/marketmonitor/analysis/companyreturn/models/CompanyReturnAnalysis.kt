package com.github.erfangc.marketmonitor.analysis.companyreturn.models

import java.time.LocalDate

data class CompanyReturnAnalysis(
        val ticker: String,
        val date: LocalDate,
        val shortTermEpsGrowths: List<ShortTermEpsGrowth>,
        val longTermGrowth: Double,
        val bvps: Double,
        val eps: Double,
        val priceToEarning: Double,
        val scenarios: List<Scenario> = emptyList(),
        val expectedReturn: Double
)
