package com.scrumdapp.gateway.handlers.exceptions

import com.scrumdapp.gateway.utils.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

data class ApiError(val code: Int, val message: String, val stackTrace: String? = null)

open class ApplicationException(
    open val code: Int,
    override val message: String,
    open val enableLogging: Boolean = false
): RuntimeException()

@RestControllerAdvice
class RuntimeExceptionHandler(
    private val exceptionUtils: ExceptionUtils
) {
    val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(Exception::class)
    public fun handleRuntimeException(exception: Exception): ResponseEntity<ApiError> {

        if (exception is ApplicationException && exception.enableLogging) {
            logger.error(exception.code.toString(), exception.message, exception.stackTrace)
        }

        val errorContent = exceptionUtils.exceptionFromThrowable(exception)
        return ResponseEntity(errorContent, HttpStatusCode.valueOf(errorContent.code))
    }
}