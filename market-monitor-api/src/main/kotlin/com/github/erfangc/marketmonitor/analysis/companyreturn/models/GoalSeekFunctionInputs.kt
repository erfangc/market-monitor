package com.github.erfangc.marketmonitor.analysis.companyreturn.models

import java.time.LocalDate

data class GoalSeekFunctionInputs(
        val date: LocalDate,
        val eps: Double,
        val price: Double,
        val bvps: Double,
        val longTermGrowth: Double,
        val shortTermEpsGrowths: List<ShortTermEpsGrowth>
)