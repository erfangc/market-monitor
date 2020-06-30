package com.github.erfangc.marketmonitor

class BadInputException(message: String) : RuntimeException(message) {
    companion object {
        /**
         * Throws an [IllegalStateException] with the given [message].
         */
        fun badInput(message: Any): Nothing = throw BadInputException(message.toString())
    }
}