package com.github.erfangc.marketmonitor.analysis.companyreturn.models

import java.time.LocalDate

data class ShortTermEps(
        val date: LocalDate,
        val eps: Double
)