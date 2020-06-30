package com.github.erfangc.marketmonitor

import java.time.Instant

data class ApiError(
        val status: Int,
        val message: String,
        val timestamp: Instant = Instant.now()
)