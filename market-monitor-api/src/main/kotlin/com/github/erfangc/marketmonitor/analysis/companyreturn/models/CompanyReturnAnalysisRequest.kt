package com.github.erfangc.marketmonitor.analysis.companyreturn.models

import java.time.LocalDate

data class CompanyReturnAnalysisRequest(
        val ticker: String,
        val date: LocalDate,
        // overrides the price from the database
        val price: Double? = null,
        val shortTermEpsGrowths: List<ShortTermEpsGrowth>,
        val longTermGrowth: Double
)
