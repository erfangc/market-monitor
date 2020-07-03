package com.github.erfangc.marketmonitor.analysis.companyreturn

import com.github.erfangc.marketmonitor.analysis.companyreturn.models.PricingFunctionInputs
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.PricingFunctionOutputs
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.pow

/**
 * Computes the theoretical price of a security
 * based on assumptions (current book value) and growth trajectory
 *
 * here discount rate is an input
 */
fun pricingFunction(inputs: PricingFunctionInputs): PricingFunctionOutputs {
    val bvps = inputs.tbvps
    val date = inputs.date
    val discountRate = inputs.discountRate
    val eps = inputs.eps
    val longTermGrowth = inputs.longTermGrowth
    val shortTermEpsEpsGrowth = inputs.shortTermEpsGrowth

    val pvsFromShortTerm = shortTermEpsEpsGrowth.fold(listOf<PvFromShortTerm>()) { acc, stg ->
        val stgDate = stg.date
        val yearFrac = yearFrac(date, stgDate)
        val discountFactor = 1 / (1 + discountRate).pow(yearFrac)
        val newEps = stg.eps
        acc + PvFromShortTerm(pv = newEps * discountFactor, valuationDate = stgDate, eps = newEps)
    }

    val lastEps = if (pvsFromShortTerm.isEmpty()) eps else pvsFromShortTerm.last().eps
    val terminalValue = lastEps / (discountRate - longTermGrowth)
    val lastValuationDate = if (pvsFromShortTerm.isEmpty()) date else pvsFromShortTerm.last().valuationDate
    val terminalDiscountFactor = 1 / (1 + discountRate).pow(yearFrac(date, lastValuationDate))
    val contributionFromTerminalValue = terminalValue * terminalDiscountFactor
    val contributionFromShortTerm = pvsFromShortTerm.sumByDouble { it.pv }
    val discountedBvps = bvps * terminalDiscountFactor

    // share price = current book value + short-term growth + terminal value
    val price = discountedBvps + contributionFromShortTerm + contributionFromTerminalValue

    val contributionFromCurrentEarnings = eps / discountRate
    val contributionFromGrowth = price - contributionFromCurrentEarnings - discountedBvps

    return PricingFunctionOutputs(
            price = price,
            contributionFromBvps = discountedBvps,
            contributionFromCurrentEarnings = contributionFromCurrentEarnings,
            contributionFromGrowth = contributionFromGrowth,
            contributionFromShortTerm = contributionFromShortTerm,
            contributionFromTerminalValue = contributionFromTerminalValue
    )
}

private fun yearFrac(from: LocalDate, to: LocalDate) = ChronoUnit.DAYS.between(from, to) / 365.2425