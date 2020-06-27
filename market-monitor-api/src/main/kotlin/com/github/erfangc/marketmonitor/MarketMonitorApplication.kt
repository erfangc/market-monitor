package com.github.erfangc.marketmonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders.*
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletResponse

@SpringBootApplication
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
	runApplication<MarketMonitorApplication>(*args)
}
