package com.github.erfangc.marketmonitor.analysis.companyreturn.models

data class PricingFunctionOutputs(
        val price: Double,
        val contributionFromBvps: Double,
        val contributionFromShortTerm: Double,
        val contributionFromTerminalValue: Double,
        val contributionFromGrowth: Double,
        val contributionFromCurrentEarnings: Double
)