package com.github.erfangc.marketmonitor.analysis.companyreturn

import java.time.LocalDate

internal data class PvFromShortTerm(val pv: Double, val valuationDate: LocalDate, val eps: Double)