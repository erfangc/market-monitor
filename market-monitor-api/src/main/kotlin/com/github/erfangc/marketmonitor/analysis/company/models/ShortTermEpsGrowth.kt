package com.github.erfangc.marketmonitor.analysis.company.models

import java.time.LocalDate

data class ShortTermEpsGrowth(
        val date: LocalDate,
        val eps: Double
)