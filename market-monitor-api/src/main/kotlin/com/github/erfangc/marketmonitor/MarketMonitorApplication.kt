package com.github.erfangc.marketmonitor

import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysis
import com.github.erfangc.marketmonitor.analysis.company.models.CompanyAnalysisRequest
import com.github.erfangc.marketmonitor.assets.Asset
import com.github.erfangc.marketmonitor.dailymetrics.models.DailyMetric
import com.github.erfangc.marketmonitor.fundamentals.models.Fundamental
import me.ntrrgc.tsGenerator.TypeScriptGenerator
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders.*
import java.io.File
import java.time.LocalDate
import javax.servlet.Filter
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
@EnableCaching
class MarketMonitorApplication {
	@Bean
	fun corsFilter(): Filter {
		return Filter { request, response, chain ->
			(response as HttpServletResponse).apply {
				setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "*")
				setHeader(ACCESS_CONTROL_ALLOW_METHODS, "*")
				setHeader(ACCESS_CONTROL_ALLOW_HEADERS, "*")
			}
			chain.doFilter(request, response)
		}
	}
}

fun main(args: Array<String>) {
	File("market-monitor-ui/src/api.d.ts").writeText(typescriptDefinitionText())
	runApplication<MarketMonitorApplication>(*args)
}

private fun typescriptDefinitionText(): String {
	return TypeScriptGenerator(
            rootClasses = setOf(
                    ApiError::class,
                    DailyMetric::class,
                    Fundamental::class,
                    CompanyAnalysis::class,
                    CompanyAnalysisRequest::class,
                    Asset::class
            ),
            mappings = mapOf(
                    LocalDate::class to "string"
            )
    ).definitionsText
}
