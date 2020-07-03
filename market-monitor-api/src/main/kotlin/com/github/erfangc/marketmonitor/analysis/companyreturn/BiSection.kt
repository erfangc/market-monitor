package com.github.erfangc.marketmonitor.analysis.companyreturn

import kotlin.math.abs

/**
 * Implements the bisection root finding method
 * @param min the lower bound of guess
 * @param max the upper bound for guess
 */
fun bisection(initialMin: Double, initialMax: Double, function: (Double) -> Double): Double {
    //
    // use bi-section method on fn(r)
    // assume r must be between 0% and 100%
    //
    val maxIter = 1000
    val tolerance = 0.001
    var iter = 0
    var epislon: Double
    var min = initialMin
    var max = initialMax
    var mid: Double
    do {
        mid = (min + max) / 2.0
        epislon = function(mid)
        if (epislon < 0) {
            max = mid
        } else if (epislon > 0) {
            min = mid
        } else {
            break
        }
        iter++
    } while (iter < maxIter && abs(epislon) > tolerance)

    if (iter == maxIter && abs(epislon) > tolerance) {
        val message = "Goal seek algorithm did not converge within $iter iterations, epislon=$epislon"
        error(message)
    }

    return mid
}
