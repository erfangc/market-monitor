package com.github.erfangc.marketmonitor.analysis.summary.models

data class SummaryDescription(
        val median: Double,
        val average: Double,
        val max: Double,
        val min: Double,
        val skew: Double,
        val stddev: Double
)