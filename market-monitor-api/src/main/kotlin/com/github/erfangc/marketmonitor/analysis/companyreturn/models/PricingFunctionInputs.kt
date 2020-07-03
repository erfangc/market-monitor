package com.github.erfangc.marketmonitor.analysis.companyreturn.models

import java.time.LocalDate

data class PricingFunctionInputs(
        val date: LocalDate,
        val eps: Double,
        val tbvps: Double,
        val longTermGrowth: Double,
        val shortTermEpsGrowth: List<ShortTermEpsGrowth>,
        val discountRate: Double
)