package com.github.erfangc.marketmonitor

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class MMControllerAdvice {

    private val log = LoggerFactory.getLogger(MMControllerAdvice::class.java)

    @ExceptionHandler(Exception::class)
    fun catchAllHandler(e: Exception): ResponseEntity<ApiError> {
        log.error("Error executing request", e)
        return ResponseEntity(
                ApiError(
                        status = 500,
                        message = e.message ?: "An error has occurred on the server"
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        )
    }

    @ExceptionHandler(BadInputException::class)
    fun badInputException(e: BadInputException): ResponseEntity<ApiError> {
        log.error("Bad input exception on request", e)
        return ResponseEntity(
                ApiError(
                        status = 400,
                        message = e.message ?: "Invalid input provided"
                ),
                HttpStatus.BAD_REQUEST
        )
    }

}