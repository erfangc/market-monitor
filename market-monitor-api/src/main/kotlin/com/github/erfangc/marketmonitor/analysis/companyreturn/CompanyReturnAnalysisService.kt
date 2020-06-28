package com.github.erfangc.marketmonitor.analysis.companyreturn

import com.github.erfangc.marketmonitor.analysis.companyreturn.models.CompanyReturnAnalysis
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.CompanyReturnAnalysisRequest
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.GoalSeekFunctionInputs
import com.github.erfangc.marketmonitor.analysis.companyreturn.models.PricingFunctionInputs
import com.github.erfangc.marketmonitor.fundamentals.FundamentalsService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs
import kotlin.math.pow

@Service
@ExperimentalStdlibApi
class CompanyReturnAnalysisService(
        private val fundamentalsService: FundamentalsService
) {

    private fun yearFrac(from: LocalDate, to: LocalDate) = ChronoUnit.DAYS.between(from, to) / 365.2425

    internal data class PvFromShortTerm(val pv: Double, val valuationDate: LocalDate, val eps: Double)

    fun companyReturnAnalysis(request: CompanyReturnAnalysisRequest): CompanyReturnAnalysis {
        val date = request.date
        val ticker = request.ticker
        val mrt = fundamentalsService.getMRT(ticker = ticker, notAfter = date)
        if (mrt.isEmpty()) {
            error("Dimension MRT Fundamental data is unavailable for $ticker on $date")
        }
        val fundamental = mrt.last()

        val eps = fundamental.eps ?: error("EPS is unavailable for $ticker on $date")
        val bvps = fundamental.bvps ?: error("BVPS is unavailable for $ticker on $date")
        val price = fundamental.price ?: error("Price is unavailable for $ticker on $date")

        val fn = goalSeekFunctionGenerator(
                inputs = GoalSeekFunctionInputs(
                        date = date,
                        bvps = bvps,
                        eps = eps,
                        price = price,
                        longTermGrowth = request.longTermGrowth,
                        shortTermEpsGrowths = request.shortTermEpsGrowths
                )
        )

        val expectedReturn = bisection(min = 0.0, max = 1.0, fn = fn)

        return CompanyReturnAnalysis(
                date = date,
                ticker = request.ticker,
                eps = eps,
                bvps = bvps,
                priceToEarning = price / eps,
                shortTermEpsGrowths = request.shortTermEpsGrowths,
                expectedReturn = expectedReturn,
                longTermGrowth = request.longTermGrowth
        )
    }

    /**
     * Creates a function that only accept discount factor as an argument (everything else is pre-computed via [inputs])
     *
     * The returned function accepts discount rate and produce the difference between predicted and actual price. It is
     * this method that the [bisection] method will operate on to find the root of
     *
     */
    private fun goalSeekFunctionGenerator(inputs: GoalSeekFunctionInputs): (Double) -> Double {
        return { discountRate ->
            pricingFunction(
                    PricingFunctionInputs(
                            date = inputs.date,
                            eps = inputs.eps,
                            bvps = inputs.bvps,
                            discountRate = discountRate,
                            longTermGrowth = inputs.longTermGrowth,
                            shortTermEpsGrowth = inputs.shortTermEpsGrowths
                    )
            ) - inputs.price
        }
    }

    /**
     * Implements the bisection root finding method
     * @param min the lower bound of guess
     * @param max the upper bound for guess
     */
    private fun bisection(min: Double, max: Double, fn: (Double) -> Double): Double {
        //
        // use bi-section method on fn(r)
        // assume r must be between 0% and 100%
        //
        val maxIter = 1000
        val tolerance = 0.001
        var iter = 0
        var guess0 = min
        var guess1 = max
        var epislon: Double
        var mid: Double

        do {
            mid = (guess0 + guess1) / 2.0
            epislon = fn(mid)
            if (epislon < 0) {
                guess1 = mid
            } else if (epislon > 0) {
                guess0 = mid
            } else {
                break
            }
            iter++
        } while (iter < maxIter && abs(epislon) > tolerance)

        return mid
    }

    /**
     * Computes the theoretical price of a security
     * based on assumptions (current book value) and growth trajectory
     *
     * here discount rate is an input
     */
    private fun pricingFunction(inputs: PricingFunctionInputs): Double {
        val bvps = inputs.bvps
        val date = inputs.date
        val discountRate = inputs.discountRate
        val eps = inputs.eps
        val longTermGrowth = inputs.longTermGrowth
        val shortTermEpsEpsGrowth = inputs.shortTermEpsGrowth

        val pvsFromShortTerm = shortTermEpsEpsGrowth.fold(listOf<PvFromShortTerm>()) { acc, stg ->
            val stgDate = stg.date
            val yearFrac = yearFrac(date, stgDate)
            val discountFactor = 1 / (1 + discountRate).pow(yearFrac)
            val lastEps = if (acc.isEmpty()) eps else acc.last().eps
            val newEps = lastEps * (1 + stg.growthRate)
            acc + PvFromShortTerm(pv = newEps * discountFactor, valuationDate = stgDate, eps = newEps)
        }

        val lastEps = if (pvsFromShortTerm.isEmpty()) eps else pvsFromShortTerm.last().eps
        val terminalValue = lastEps / (discountRate - longTermGrowth)
        val lastValuationDate = if (pvsFromShortTerm.isEmpty()) date else pvsFromShortTerm.last().valuationDate
        val terminalDiscountFactor = 1 / (1 + discountRate).pow(yearFrac(date, lastValuationDate))
        val discountedTerminalValue = terminalValue * terminalDiscountFactor
        val pvFromShortTerm = pvsFromShortTerm.sumByDouble { it.pv }

        // share price = current book value + short-term growth + terminal value
        return bvps + pvFromShortTerm + discountedTerminalValue
    }

}