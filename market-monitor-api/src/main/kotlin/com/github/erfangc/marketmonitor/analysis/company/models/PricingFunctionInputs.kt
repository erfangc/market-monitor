package com.github.erfangc.marketmonitor.analysis.company.models

import java.time.LocalDate

data class PricingFunctionInputs(
        val date: LocalDate,
        val eps: Double,
        val tbvps: Double,
        val longTermGrowth: Double,
        val shortTermEpsGrowth: List<ShortTermEpsGrowth>,
        val discountRate: Double
)