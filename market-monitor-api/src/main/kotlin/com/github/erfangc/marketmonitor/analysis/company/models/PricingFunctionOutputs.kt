package com.github.erfangc.marketmonitor.analysis.company.models

data class PricingFunctionOutputs(
        val price: Double,
        val contributionFromBvps: Double,
        val contributionFromShortTerm: Double,
        val contributionFromTerminalValue: Double,
        val contributionFromGrowth: Double,
        val contributionFromCurrentEarnings: Double
)