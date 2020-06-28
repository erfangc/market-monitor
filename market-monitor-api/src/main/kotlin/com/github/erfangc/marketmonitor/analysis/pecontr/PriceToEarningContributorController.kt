package com.github.erfangc.marketmonitor.analysis.pecontr

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@ExperimentalStdlibApi
@RequestMapping("price-to-earning-contributors")
class PriceToEarningContributorController(
        private val priceToEarningContributorService: PriceToEarningContributorService
) {
    @GetMapping
    fun priceToEarningContributor(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate?,
            @RequestParam(required = false) sector: String? = null,
            @RequestParam(required = false) industry: String? = null
    ): List<PriceToEarningContributor> {
        return priceToEarningContributorService.priceToEarningContributor(date, sector, industry)
    }
}