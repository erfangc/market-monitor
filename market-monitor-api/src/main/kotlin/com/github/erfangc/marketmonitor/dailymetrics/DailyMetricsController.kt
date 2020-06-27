package com.github.erfangc.marketmonitor.dailymetrics

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
    @PostMapping("_recreate_table")
    fun recreateTable() {
        dailyMetricsService.recreateTable()
    }

    @PostMapping("_load_for_today")
    fun loadForToday(){
        dailyMetricsService.loadForToday()
    }

    @PostMapping("_load_for_period")
    fun loadForPeriod(@RequestParam startDate: LocalDate? = null, @RequestParam endDate: LocalDate = LocalDate.now()) {
        dailyMetricsService.loadForPeriod(startDate, endDate)
    }

    @PostMapping("_load_for_date")
    fun loadForDate(@RequestParam date: LocalDate) {
        dailyMetricsService.loadForDate(date)
    }
}