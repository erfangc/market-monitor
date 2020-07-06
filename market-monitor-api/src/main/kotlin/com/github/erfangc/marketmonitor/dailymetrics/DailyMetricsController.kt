package com.github.erfangc.marketmonitor.dailymetrics

import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@ExperimentalStdlibApi
@RequestMapping("daily-metrics")
class DailyMetricsController constructor(
        private val dailyMetricsService: DailyMetricsService
) {
    @PostMapping("_bootstrap")
    fun bootstrap() {
        dailyMetricsService.bootstrap()
    }

    @PostMapping("_load-for-period")
    fun loadForPeriod(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate? = null,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate? = null
    ) {
        dailyMetricsService.loadForPeriod(startDate, endDate)
    }

    @PostMapping("_load-for-]date")
    fun loadForDate(@RequestParam date: LocalDate) {
        dailyMetricsService.loadForDate(date)
    }
}