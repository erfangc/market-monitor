package com.github.erfangc.marketmonitor.fundamentals

import com.github.erfangc.marketmonitor.fundamentals.models.Fundamental
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@ExperimentalStdlibApi
@RequestMapping("fundamentals")
class FundamentalsController(private val fundamentalsService: FundamentalsService) {

    @GetMapping("{ticker}/MRQ")
    fun getMRQ(
            @PathVariable ticker: String,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) notAfter: LocalDate? = null
    ): List<Fundamental> {
        return fundamentalsService.getMRQ(ticker, notAfter)
    }

    @GetMapping("{ticker}/MRT")
    fun getMRT(
            @PathVariable ticker: String,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) notAfter: LocalDate? = null
    ): List<Fundamental> {
        return fundamentalsService.getMRT(ticker, notAfter)
    }

    @GetMapping("{ticker}/MRY")
    fun getMRY(
            @PathVariable ticker: String,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) notAfter: LocalDate? = null
    ): List<Fundamental> {
        return fundamentalsService.getMRY(ticker, notAfter)
    }

    @PostMapping
    fun bootstrap() {
        fundamentalsService.bootstrap()
    }
}