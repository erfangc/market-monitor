package com.github.erfangc.marketmonitor.fundamentals

import com.github.erfangc.marketmonitor.fundamentals.models.Fundamental
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@ExperimentalStdlibApi
@RequestMapping("fundamentals")
class FundamentalsController(private val fundamentalsService: FundamentalsService) {
    @GetMapping("{ticker}")
    fun getMrq(
            @PathVariable ticker: String,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) notAfter: LocalDate? = null
    ): List<Fundamental> {
        return fundamentalsService.getMrq(ticker, notAfter)
    }

    @PostMapping
    fun load() {
        fundamentalsService.load()
    }
}