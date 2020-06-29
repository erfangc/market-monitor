package com.github.erfangc.marketmonitor.analysis.companyreturn.models

import java.time.LocalDate

data class ShortTermEpsGrowth(
        val date: LocalDate,
        val growthRate: Double? = null,
        val eps: Double? = null
)