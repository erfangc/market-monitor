package com.github.erfangc.marketmonitor.analysis.companyreturn.models

data class Scenario(
        val longTermGrowth: Double,
        val price: Double,
        val expectedReturn: Double
)